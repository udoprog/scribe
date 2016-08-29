package eu.toolchain.scribe;

import eu.toolchain.scribe.creatormethod.CreatorMethod;
import eu.toolchain.scribe.entitymapper.CreatorMethodDetector;
import eu.toolchain.scribe.entitymapper.DecodeValueDetector;
import eu.toolchain.scribe.entitymapper.EncodeValueDetector;
import eu.toolchain.scribe.entitymapper.EntityMappingDetector;
import eu.toolchain.scribe.entitymapper.FieldFlagDetector;
import eu.toolchain.scribe.entitymapper.FieldNameDetector;
import eu.toolchain.scribe.entitymapper.FieldReaderDetector;
import eu.toolchain.scribe.entitymapper.SubType;
import eu.toolchain.scribe.entitymapper.SubTypesDetector;
import eu.toolchain.scribe.entitymapper.TypeAliasDetector;
import eu.toolchain.scribe.entitymapper.TypeNameDetector;
import eu.toolchain.scribe.entitymapping.EntityMapping;
import eu.toolchain.scribe.fieldreader.FieldReader;
import eu.toolchain.scribe.typealias.TypeAlias;
import eu.toolchain.scribe.typemapper.TypeMapper;
import eu.toolchain.scribe.typemapping.ConcreteEntityTypeMapping;
import eu.toolchain.scribe.typemapping.DecodeValue;
import eu.toolchain.scribe.typemapping.EncodeValue;
import eu.toolchain.scribe.typemapping.EntityTypeMapping;
import eu.toolchain.scribe.typemapping.PropertyAbstractEntityTypeMapping;
import eu.toolchain.scribe.typemapping.TypeMapping;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
public class EntityMapper implements EntityResolver {
  private static final Comparator<Match<?>> BY_SCORE_COMPARATOR =
      (a, b) -> Integer.compare(b.getPriority().ordinal(), a.getPriority().ordinal());

  private final List<TypeAliasDetector> typeAliasDetectors;
  private final List<TypeMapper> typeMapperDetectors;
  private final List<FieldReaderDetector> fieldReaderDetectors;
  private final List<CreatorMethodDetector> creatorMethodDetectors;
  private final List<EntityMappingDetector> entityMappingDetectors;
  private final List<SubTypesDetector> subTypesDetectors;
  private final List<EncodeValueDetector> encodeValueDetectors;
  private final List<DecodeValueDetector> decodeValueDetectors;
  private final List<FieldNameDetector> fieldNameDetectors;
  private final List<FieldFlagDetector> fieldFlagDetectors;
  private final List<TypeNameDetector> typeNameDetectors;
  private final Map<Class<? extends Option>, Option> options;

  private final ConcurrentMap<EntityKey, TypeMapping> cache = new ConcurrentHashMap<>();
  private final Object resolverLock = new Object();

  /**
   * {@inheritDoc}
   */
  @Override
  public <Target> TypeStreamEncoderProvider<Target> streamEncoderFor(
      final StreamEncoderFactory<Target> factory
  ) {
    return new TypeStreamEncoderProvider<Target>() {
      @Override
      public StreamEncoder<Target, Object> newStreamEncoder(Type type) {
        return mapping(JavaType.of(type))
            .newStreamEncoder(EntityMapper.this, Flags.empty(), factory)
            .orElseThrow(() -> new IllegalArgumentException(
                "Unable to resolve encoding for type (" + type + ")"));
      }

      @SuppressWarnings("unchecked")
      @Override
      public <Source> StreamEncoder<Target, Source> newStreamEncoder(Class<Source> type) {
        return (StreamEncoder<Target, Source>) newStreamEncoder((Type) type);
      }

      @SuppressWarnings("unchecked")
      @Override
      public <Source> StreamEncoder<Target, Source> newStreamEncoder(
          TypeReference<Source> type
      ) {
        return (StreamEncoder<Target, Source>) newStreamEncoder(type.getType());
      }
    };
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public <Target> TypeEncoderProvider<Target> encoderFor(
      final EncoderFactory<Target> factory
  ) {
    return new TypeEncoderProvider<Target>() {
      @Override
      public Encoder<Target, Object> newEncoder(Type type) {
        return mapping(JavaType.of(type))
            .newEncoder(EntityMapper.this, Flags.empty(), factory)
            .orElseThrow(() -> new IllegalArgumentException(
                "Unable to resolve encoding for type (" + type + ")"));
      }

      @SuppressWarnings("unchecked")
      @Override
      public <Source> Encoder<Target, Source> newEncoder(Class<Source> type) {
        return (Encoder<Target, Source>) newEncoder((Type) type);
      }

      @SuppressWarnings("unchecked")
      @Override
      public <Source> Encoder<Target, Source> newEncoder(TypeReference<Source> type) {
        return (Encoder<Target, Source>) newEncoder(type.getType());
      }
    };
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public <Target> TypeDecoderProvider<Target> decoderFor(
      final DecoderFactory<Target> factory
  ) {
    return new TypeDecoderProvider<Target>() {
      @Override
      public Decoder<Target, Object> newDecoder(Type type) {
        return mapping(JavaType.of(type))
            .newDecoder(EntityMapper.this, Flags.empty(), factory)
            .orElseThrow(() -> new IllegalArgumentException(
                "Unable to resolve encoding for type (" + type + ")"));
      }

      @SuppressWarnings("unchecked")
      @Override
      public <Source> Decoder<Target, Source> newDecoder(Class<Source> type) {
        return (Decoder<Target, Source>) newDecoder((Type) type);
      }

      @SuppressWarnings("unchecked")
      @Override
      public <Source> Decoder<Target, Source> newDecoder(TypeReference<Source> type) {
        return (Decoder<Target, Source>) newDecoder(type.getType());
      }
    };
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public TypeMapping mapping(final JavaType type, final Annotations annotations) {
    final EntityKey key = new EntityKey(type, annotations);

    final TypeMapping mapping = cache.get(key);

    if (mapping != null) {
      return mapping;
    }

    synchronized (resolverLock) {
      final TypeMapping candidate = cache.get(key);

      if (candidate != null) {
        return candidate;
      }

      final TypeMapping newMapping = resolveAliasing(type, annotations);

      cache.put(key, newMapping);
      newMapping.initialize(this);
      return newMapping;
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public TypeMapping mapping(final JavaType type) {
    return mapping(type, Annotations.empty());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Optional<? extends CreatorMethod> detectCreatorMethod(JavaType type) {
    return firstPriorityMatch(creatorMethodDetectors.stream(), c -> c.detect(this, type));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Optional<FieldReader> detectFieldReader(
      final JavaType type, final String fieldName, final JavaType fieldType
  ) {
    return firstPriorityMatch(fieldReaderDetectors.stream(),
        c -> c.detect(type, fieldName, fieldType));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Optional<EntityMapping> detectEntityMapping(JavaType type) {
    return firstPriorityMatch(entityMappingDetectors.stream(), d -> d.detect(this, type));
  }

  public List<SubType> resolveSubTypes(final JavaType type) {
    return firstPriorityMatch(subTypesDetectors.stream(), d -> d.detect(this, type)).orElseGet(
        Collections::emptyList);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Optional<EncodeValue> detectEncodeValue(final JavaType type) {
    return firstPriorityMatch(encodeValueDetectors.stream(), d -> d.detect(this, type));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Optional<DecodeValue> detectDecodeValue(final JavaType type, final JavaType fieldType) {
    return firstPriorityMatch(decodeValueDetectors.stream(), d -> d.detect(this, type, fieldType));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Optional<String> detectFieldName(JavaType type, Annotations annotations) {
    return firstPriorityMatch(fieldNameDetectors.stream(), d -> d.detect(this, type, annotations));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Optional<String> detectTypeName(JavaType type) {
    return firstPriorityMatch(typeNameDetectors.stream(), d -> d.detect(this, type));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<EntityField> detectExecutableFields(final ExecutableType executable) {
    final ArrayList<EntityField> fields = new ArrayList<>();

    int index = 0;

    for (final JavaType.Parameter p : executable.getParameters()) {
      final int i = index++;

      final Annotations annotations = Annotations.of(p.getAnnotationStream());
      final JavaType type = p.getParameterType();
      final Optional<String> name = detectFieldName(type, annotations);

      fields.add(new EntityField(false, i, annotations, type, name));
    }

    return fields;
  }

  @Override
  public Flags detectFieldFlags(final JavaType type, final Annotations annotations) {
    return Flags.copyOf(fieldFlagDetectors
        .stream()
        .flatMap(d -> d.detect(this, type, annotations))
        .collect(Collectors.toList()));
  }

  /**
   * {@inheritDoc}
   */
  @SuppressWarnings("unchecked")
  @Override
  public <O extends Option> Optional<O> getOption(final Class<O> option) {
    return Optional.ofNullable((O) options.get(option));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public <O extends Option> boolean isOptionPresent(final O option) {
    return options.values().contains(option);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public EntityMapper withOptions(final Option... options) {
    if (options.length == 0) {
      return this;
    }

    final Builder builder = toBuilder();
    Arrays.stream(options).forEach(builder::option);
    return builder.build();
  }

  @Override
  public Annotations detectImmediateAnnotations(final JavaType type, final String fieldName) {
    return type
        .getField(fieldName)
        .map(f -> Annotations.of(f.getAnnotationStream()))
        .orElseGet(Annotations::empty);
  }

  private TypeMapping resolveAliasing(final JavaType type, final Annotations annotations) {
    final List<TypeAlias> aliasing = resolveTypeAliases(type, annotations);

    if (aliasing.isEmpty()) {
      return resolveTypeMapping(type);
    } else {
      return applyTypeAliases(aliasing);
    }
  }

  private TypeMapping applyTypeAliases(final List<TypeAlias> aliasing) {
    final TypeAlias lastAlias = aliasing.get(aliasing.size() - 1);

    final ListIterator<TypeAlias> it = aliasing.listIterator(aliasing.size());

    TypeMapping lastMapping = resolveTypeMapping(lastAlias.getFromType());

    while (it.hasPrevious()) {
      lastMapping = it.previous().apply(lastMapping);
    }

    return lastMapping;
  }

  private TypeMapping resolveTypeMapping(final JavaType sourceType) {
    return firstPriorityMatch(typeMapperDetectors.stream(), m -> m.map(this, sourceType)).orElseGet(
        () -> resolveBean(sourceType));
  }

  private List<TypeAlias> resolveTypeAliases(final JavaType type, final Annotations annotations) {
    final ArrayList<TypeAlias> aliasing = new ArrayList<>();

    final List<JavaType> seen = new ArrayList<>();
    seen.add(type);

    JavaType current = type;

    while (true) {
      final JavaType t = current;
      final Optional<TypeAlias> m =
          firstMatch(typeAliasDetectors.stream(), a -> a.apply(t, annotations));

      if (!m.isPresent()) {
        break;
      }

      final TypeAlias alias = m.get();

      if (seen.contains(alias.getFromType())) {
        seen.add(alias.getToType());

        final StringJoiner joiner = new StringJoiner(" -> ", "", "");
        seen.stream().map(Object::toString).forEach(joiner::add);

        throw new IllegalArgumentException("Cycle in aliasing detected: " + joiner.toString());
      }

      seen.add(alias.getFromType());

      aliasing.add(alias);
      current = alias.getFromType();
    }

    return aliasing;
  }

  private <Target, Source> Optional<Source> firstMatch(
      Stream<Target> alternatives, Function<Target, Stream<Source>> map
  ) {
    final List<Source> results = alternatives.flatMap(map).collect(Collectors.toList());

    if (results.size() > 1) {
      throw new IllegalArgumentException("Found multiple matches for type: " + results);
    }

    return results.stream().findFirst();
  }

  private <Target, Source> Optional<Source> firstPriorityMatch(
      Stream<Target> alternatives, Function<Target, Stream<Match<Source>>> map
  ) {
    final List<Match<Source>> results = alternatives.flatMap(map).collect(Collectors.toList());

    final List<Match<Source>> sorted = new ArrayList<>(results);
    Collections.sort(sorted, BY_SCORE_COMPARATOR);

    if (results.size() > 1) {
      if (sorted.get(0).getPriority() == sorted.get(1).getPriority()) {
        throw new IllegalArgumentException(
            "Found multiple matches with the same priority: " + sorted);
      }
    }

    return sorted.stream().map(Match::getValue).findFirst();
  }

  private TypeMapping resolveBean(final JavaType type) {
    return resolveEncodeValue(type).orElseGet(() -> {
      final Optional<String> typeName = detectTypeName(type);

      if (type.isAbstract()) {
        return doAbstract(type, typeName);
      }

      return doConcrete(type, typeName);
    });
  }

  private Optional<TypeMapping> resolveEncodeValue(final JavaType type) {
    return detectEncodeValue(type).<TypeMapping>map(encodeValue -> {
      final TypeMapping target = encodeValue.getTargetMapping();

      final DecodeValue decodeValue = detectDecodeValue(type, target.getType()).orElseThrow(
          () -> new IllegalArgumentException(
              "Value encoder detected, but no corresponding decoder: " + type));

      return new ValueTypeMapping(encodeValue, decodeValue);
    });
  }

  private EntityTypeMapping doAbstract(
      final JavaType type, final Optional<String> typeName
  ) {
    final List<SubType> subTypes = resolveSubTypes(type);

    return new PropertyAbstractEntityTypeMapping(type, typeName, subTypes, Optional.empty());
  }

  private EntityTypeMapping doConcrete(final JavaType type, final Optional<String> typeName) {
    return new ConcreteEntityTypeMapping(type, typeName);
  }

  @Data
  public static class EntityKey {
    private final JavaType type;
    private final Annotations annotations;
  }

  public Builder toBuilder() {
    return new Builder(new ArrayList<>(typeAliasDetectors), new ArrayList<>(typeMapperDetectors),
        new ArrayList<>(fieldReaderDetectors), new ArrayList<>(creatorMethodDetectors),
        new ArrayList<>(entityMappingDetectors), new ArrayList<>(subTypesDetectors),
        new ArrayList<>(encodeValueDetectors), new ArrayList<>(decodeValueDetectors),
        new ArrayList<>(fieldNameDetectors), new ArrayList<>(fieldFlagDetectors),
        new ArrayList<>(typeNameDetectors), new HashSet<>(options.values()));
  }

  public static EntityMapperBuilder<EntityMapper> builder() {
    return new Builder();
  }

  public static EntityMapperBuilder<EntityMapper> defaultBuilder() {
    return builder().register(new DefaultModule());
  }

  public static EntityMapperBuilder<EntityMapper> nativeBuilder() {
    return defaultBuilder().register(new NativeAnnotationsModule());
  }

  @AllArgsConstructor
  public static class Builder implements EntityMapperBuilder<EntityMapper> {
    private final ArrayList<TypeAliasDetector> typeAliasDetectors;
    private final ArrayList<TypeMapper> typeMapperDetectors;
    private final ArrayList<FieldReaderDetector> fieldReaderDetectors;
    private final ArrayList<CreatorMethodDetector> creatorMethodDetectors;
    private final ArrayList<EntityMappingDetector> entityMappingDetectors;
    private final ArrayList<SubTypesDetector> subTypesDetectors;
    private final ArrayList<EncodeValueDetector> encodeValueDetectors;
    private final ArrayList<DecodeValueDetector> decodeValueDetectors;
    private final ArrayList<FieldNameDetector> fieldNameDetectors;
    private final ArrayList<FieldFlagDetector> fieldFlagDetectors;
    private final ArrayList<TypeNameDetector> typeNameDetectors;
    private final HashSet<Option> options;

    public Builder() {
      typeAliasDetectors = new ArrayList<>();
      typeMapperDetectors = new ArrayList<>();
      fieldReaderDetectors = new ArrayList<>();
      creatorMethodDetectors = new ArrayList<>();
      entityMappingDetectors = new ArrayList<>();
      subTypesDetectors = new ArrayList<>();
      encodeValueDetectors = new ArrayList<>();
      decodeValueDetectors = new ArrayList<>();
      fieldNameDetectors = new ArrayList<>();
      fieldFlagDetectors = new ArrayList<>();
      typeNameDetectors = new ArrayList<>();
      options = new HashSet<>();
    }

    @Override
    public Builder typeAliasDetector(TypeAliasDetector typeAliasDetector) {
      this.typeAliasDetectors.add(typeAliasDetector);
      return this;
    }

    @Override
    public Builder typeMapper(TypeMapper typeMapper) {
      this.typeMapperDetectors.add(typeMapper);
      return this;
    }

    @Override
    public Builder fieldReaderDetector(FieldReaderDetector fieldReader) {
      this.fieldReaderDetectors.add(fieldReader);
      return this;
    }

    @Override
    public Builder creatorMethodDetector(CreatorMethodDetector creatorMethod) {
      this.creatorMethodDetectors.add(creatorMethod);
      return this;
    }

    @Override
    public Builder entityMappingDetector(EntityMappingDetector binding) {
      this.entityMappingDetectors.add(binding);
      return this;
    }

    public Builder subTypesDetector(SubTypesDetector subTypeDetector) {
      this.subTypesDetectors.add(subTypeDetector);
      return this;
    }

    @Override
    public Builder encodeValueDetector(EncodeValueDetector encodeValueDetector) {
      this.encodeValueDetectors.add(encodeValueDetector);
      return this;
    }

    @Override
    public Builder decodeValueDetector(DecodeValueDetector decodeValueDetector) {
      this.decodeValueDetectors.add(decodeValueDetector);
      return this;
    }

    @Override
    public Builder fieldNameDetector(FieldNameDetector fieldNameDetector) {
      this.fieldNameDetectors.add(fieldNameDetector);
      return this;
    }

    @Override
    public Builder fieldFlagDetector(FieldFlagDetector fieldFlagDetector) {
      this.fieldFlagDetectors.add(fieldFlagDetector);
      return this;
    }

    @Override
    public Builder typeNameDetector(TypeNameDetector typeNameDetector) {
      this.typeNameDetectors.add(typeNameDetector);
      return this;
    }

    @Override
    public Builder option(Option option) {
      this.options.add(option);
      return this;
    }

    @Override
    public EntityMapper build() {
      final Map<Class<? extends Option>, Option> options =
          this.options.stream().collect(Collectors.toMap(o -> o.getClass(), Function.identity()));

      return new EntityMapper(Collections.unmodifiableList(new ArrayList<>(typeAliasDetectors)),
          Collections.unmodifiableList(new ArrayList<>(typeMapperDetectors)),
          Collections.unmodifiableList(new ArrayList<>(fieldReaderDetectors)),
          Collections.unmodifiableList(new ArrayList<>(creatorMethodDetectors)),
          Collections.unmodifiableList(new ArrayList<>(entityMappingDetectors)),
          Collections.unmodifiableList(new ArrayList<>(subTypesDetectors)),
          Collections.unmodifiableList(new ArrayList<>(encodeValueDetectors)),
          Collections.unmodifiableList(new ArrayList<>(decodeValueDetectors)),
          Collections.unmodifiableList(new ArrayList<>(fieldNameDetectors)),
          Collections.unmodifiableList(new ArrayList<>(fieldFlagDetectors)),
          Collections.unmodifiableList(new ArrayList<>(typeNameDetectors)),
          Collections.unmodifiableMap(options));
    }

    @Override
    public EntityMapperBuilder<EntityMapper> register(final Module module) {
      return module.register(this);
    }
  }
}
