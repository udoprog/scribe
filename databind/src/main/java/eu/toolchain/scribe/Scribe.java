package eu.toolchain.scribe;

import eu.toolchain.scribe.detector.ClassEncodingDetector;
import eu.toolchain.scribe.detector.DecodeValueDetector;
import eu.toolchain.scribe.detector.EncodeValueDetector;
import eu.toolchain.scribe.detector.FieldNameDetector;
import eu.toolchain.scribe.detector.FieldReaderDetector;
import eu.toolchain.scribe.detector.FlagDetector;
import eu.toolchain.scribe.detector.InstanceBuilderDetector;
import eu.toolchain.scribe.detector.MappingDetector;
import eu.toolchain.scribe.detector.Match;
import eu.toolchain.scribe.detector.SubTypesDetector;
import eu.toolchain.scribe.detector.TypeAliasDetector;
import eu.toolchain.scribe.detector.TypeNameDetector;
import eu.toolchain.scribe.reflection.Annotations;
import eu.toolchain.scribe.reflection.JavaType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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

import static eu.toolchain.scribe.Streams.streamRequireOne;

@RequiredArgsConstructor
public class Scribe implements EntityResolver {
  private final List<TypeAliasDetector<Object, Object>> typeAliasDetectors;
  private final List<MappingDetector> mappingDetectors;
  private final List<FieldReaderDetector> fieldReaderDetectors;
  private final List<InstanceBuilderDetector> instanceBuilderDetectors;
  private final List<ClassEncodingDetector> classEncodingDetectors;
  private final List<SubTypesDetector> subTypesDetectors;
  private final List<EncodeValueDetector> encodeValueDetectors;
  private final List<DecodeValueDetector> decodeValueDetectors;
  private final List<FieldNameDetector> fieldNameDetectors;
  private final List<FlagDetector> flagDetectors;
  private final List<TypeNameDetector> typeNameDetectors;
  private final Map<Class<? extends Option>, Option> options;

  private final ConcurrentMap<EntityKey, Mapping<Object>> cache = new ConcurrentHashMap<>();
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
        return streamRequireOne(mapping(JavaType.of(type)).newStreamEncoder(Scribe.this, factory),
            values -> new IllegalArgumentException(
                "Expected one stream encoder for type (" + type + ") but got (" + values + ")"));
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
  public <Target, EntityTarget> TypeEncoderProvider<Target> encoderFor(
      final EncoderFactory<Target, EntityTarget> factory
  ) {
    return new TypeEncoderProvider<Target>() {
      @Override
      public Encoder<Target, Object> newEncoderForType(Type type) {
        return streamRequireOne(mapping(JavaType.of(type)).newEncoder(Scribe.this, factory),
            values -> new IllegalArgumentException(
                "Expected one encoder for type (" + type + ") but got (" + values + ")"));
      }

      @SuppressWarnings("unchecked")
      @Override
      public <Source> Encoder<Target, Source> newEncoder(Class<Source> type) {
        return (Encoder<Target, Source>) newEncoderForType((Type) type);
      }

      @SuppressWarnings("unchecked")
      @Override
      public <Source> Encoder<Target, Source> newEncoder(TypeReference<Source> type) {
        return (Encoder<Target, Source>) newEncoderForType(type.getType());
      }
    };
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public <Target, EntityTarget> TypeDecoderProvider<Target> decoderFor(
      final DecoderFactory<Target, EntityTarget> factory
  ) {
    return new TypeDecoderProvider<Target>() {
      @Override
      public Decoder<Target, Object> newDecoderForType(Type type) {
        return streamRequireOne(mapping(JavaType.of(type)).newDecoder(Scribe.this, factory),
            values -> new IllegalArgumentException(
                "Expected one decoder for type (" + type + ") but got (" + values + ")"));
      }

      @SuppressWarnings("unchecked")
      @Override
      public <Source> Decoder<Target, Source> newDecoder(Class<Source> type) {
        return (Decoder<Target, Source>) newDecoderForType((Type) type);
      }

      @SuppressWarnings("unchecked")
      @Override
      public <Source> Decoder<Target, Source> newDecoder(TypeReference<Source> type) {
        return (Decoder<Target, Source>) newDecoderForType(type.getType());
      }
    };
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Mapping<Object> mapping(final JavaType type, final Annotations annotations) {
    final EntityKey key = new EntityKey(type, annotations);

    final Mapping<Object> mapping = cache.get(key);

    if (mapping != null) {
      return mapping;
    }

    synchronized (resolverLock) {
      final Mapping<Object> candidate = cache.get(key);

      if (candidate != null) {
        return candidate;
      }

      final Mapping<Object> newMapping = resolveAliasing(type, annotations);

      cache.put(key, newMapping);
      newMapping.postCacheInitialize(this);
      return newMapping;
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Mapping<Object> mapping(final JavaType type) {
    return mapping(type, Annotations.empty());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Optional<InstanceBuilder<Object>> detectInstanceBuilder(JavaType type) {
    return Match.bestUniqueMatch(instanceBuilderDetectors.stream(), c -> c.detect(this, type));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Optional<FieldReader> detectFieldReader(
      final JavaType type, final String fieldName, final JavaType fieldType
  ) {
    return Match.bestUniqueMatch(fieldReaderDetectors.stream(),
        c -> c.detect(type, fieldName, fieldType));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Optional<ClassEncoding<Object>> detectEntityMapping(JavaType type) {
    return Match.bestUniqueMatch(classEncodingDetectors.stream(), d -> d.detect(this, type));
  }

  public List<SubType<Object>> resolveSubTypes(final JavaType type) {
    return Match
        .bestUniqueMatch(subTypesDetectors.stream(), d -> d.detect(this, type))
        .orElseGet(Collections::emptyList);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Optional<EncodeValue<Object>> detectEncodeValue(final JavaType type) {
    return Match.bestUniqueMatch(encodeValueDetectors.stream(), d -> d.detect(this, type));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Optional<DecodeValue<Object>> detectDecodeValue(
      final JavaType type, final JavaType fieldType
  ) {
    return Match.bestUniqueMatch(decodeValueDetectors.stream(),
        d -> d.detect(this, type, fieldType));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Optional<String> detectFieldName(JavaType type, Annotations annotations) {
    return Match.bestUniqueMatch(fieldNameDetectors.stream(),
        d -> d.detect(this, type, annotations));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Optional<String> detectTypeName(JavaType type) {
    return Match.bestUniqueMatch(typeNameDetectors.stream(), d -> d.detect(this, type));
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
    return Flags.copyOf(flagDetectors
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
  public Scribe withOptions(final Option... options) {
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

  private Mapping<Object> resolveAliasing(final JavaType type, final Annotations annotations) {
    final List<TypeAlias<Object, Object>> aliasing = resolveTypeAliases(type, annotations);

    if (aliasing.isEmpty()) {
      return resolveTypeMapping(type);
    } else {
      return applyTypeAliases(aliasing);
    }
  }

  private Mapping<Object> applyTypeAliases(final List<TypeAlias<Object, Object>> aliasing) {
    final TypeAlias<Object, Object> lastAlias = aliasing.get(aliasing.size() - 1);

    final ListIterator<TypeAlias<Object, Object>> it = aliasing.listIterator(aliasing.size());

    Mapping<Object> lastMapping = resolveTypeMapping(lastAlias.getFromType());

    while (it.hasPrevious()) {
      lastMapping = it.previous().apply(lastMapping);
    }

    return lastMapping;
  }

  private Mapping<Object> resolveTypeMapping(final JavaType sourceType) {
    return Match
        .bestUniqueMatch(mappingDetectors.stream(), m -> m.detect(this, sourceType))
        .orElseGet(() -> resolveBean(sourceType));
  }

  private List<TypeAlias<Object, Object>> resolveTypeAliases(
      final JavaType type, final Annotations annotations
  ) {
    final ArrayList<TypeAlias<Object, Object>> aliasing = new ArrayList<>();

    final List<JavaType> seen = new ArrayList<>();
    seen.add(type);

    JavaType current = type;

    while (true) {
      final JavaType t = current;
      final Optional<TypeAlias<Object, Object>> m =
          firstMatch(typeAliasDetectors.stream(), a -> a.detect(t, annotations));

      if (!m.isPresent()) {
        break;
      }

      final TypeAlias<Object, Object> alias = m.get();

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

  private Mapping<Object> resolveBean(final JavaType type) {
    return resolveEncodeValue(type).orElseGet(() -> {
      final Optional<String> typeName = detectTypeName(type);

      if (type.isAbstract()) {
        return doAbstract(type, typeName);
      }

      return doConcrete(type, typeName);
    });
  }

  private Optional<Mapping<Object>> resolveEncodeValue(final JavaType type) {
    return detectEncodeValue(type).map(encodeValue -> {
      final Mapping<Object> target = encodeValue.getTargetMapping();

      final DecodeValue<Object> decodeValue = detectDecodeValue(type, target.getType()).orElseThrow(
          () -> new IllegalArgumentException("Value encoder (" + encodeValue +
              ") detected, but no corresponding decoder for type (" + type + ")"));

      return new ValueMapping<>(encodeValue, decodeValue);
    });
  }

  private ClassMapping<Object> doAbstract(
      final JavaType type, final Optional<String> typeName
  ) {
    final List<SubType<Object>> subTypes = resolveSubTypes(type);

    return new AbstractClassMapping<>(type, typeName, subTypes, Optional.empty());
  }

  private ClassMapping<Object> doConcrete(final JavaType type, final Optional<String> typeName) {
    return new ConcreteClassMapping<>(type, typeName);
  }

  @Data
  public static class EntityKey {
    private final JavaType type;
    private final Annotations annotations;
  }

  public Builder toBuilder() {
    return new Builder(new ArrayList<>(typeAliasDetectors), new ArrayList<>(mappingDetectors),
        new ArrayList<>(fieldReaderDetectors), new ArrayList<>(instanceBuilderDetectors),
        new ArrayList<>(classEncodingDetectors), new ArrayList<>(subTypesDetectors),
        new ArrayList<>(encodeValueDetectors), new ArrayList<>(decodeValueDetectors),
        new ArrayList<>(fieldNameDetectors), new ArrayList<>(flagDetectors),
        new ArrayList<>(typeNameDetectors), new HashSet<>(options.values()));
  }

  public static Builder builder() {
    return new Builder();
  }

  public static Builder defaultBuilder() {
    return builder().install(new DefaultModule());
  }

  public static Builder nativeBuilder() {
    return defaultBuilder().install(new NativeAnnotationsModule());
  }

  @AllArgsConstructor
  public static class Builder implements ScribeBuilder {
    private final ArrayList<TypeAliasDetector<Object, Object>> typeAliasDetectors;
    private final ArrayList<MappingDetector> mappingDetectors;
    private final ArrayList<FieldReaderDetector> fieldReaderDetectors;
    private final ArrayList<InstanceBuilderDetector> instanceBuilderDetectors;
    private final ArrayList<ClassEncodingDetector> classEncodingDetectors;
    private final ArrayList<SubTypesDetector> subTypesDetectors;
    private final ArrayList<EncodeValueDetector> encodeValueDetectors;
    private final ArrayList<DecodeValueDetector> decodeValueDetectors;
    private final ArrayList<FieldNameDetector> fieldNameDetectors;
    private final ArrayList<FlagDetector> flagDetectors;
    private final ArrayList<TypeNameDetector> typeNameDetectors;
    private final HashSet<Option> options;

    public Builder() {
      typeAliasDetectors = new ArrayList<>();
      mappingDetectors = new ArrayList<>();
      fieldReaderDetectors = new ArrayList<>();
      instanceBuilderDetectors = new ArrayList<>();
      classEncodingDetectors = new ArrayList<>();
      subTypesDetectors = new ArrayList<>();
      encodeValueDetectors = new ArrayList<>();
      decodeValueDetectors = new ArrayList<>();
      fieldNameDetectors = new ArrayList<>();
      flagDetectors = new ArrayList<>();
      typeNameDetectors = new ArrayList<>();
      options = new HashSet<>();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <From, To> Builder typeAlias(final TypeAliasDetector<From, To> detector) {
      this.typeAliasDetectors.add((TypeAliasDetector<Object, Object>) detector);
      return this;
    }

    @Override
    public Builder mapping(MappingDetector detector) {
      this.mappingDetectors.add(detector);
      return this;
    }

    @Override
    public Builder fieldReader(FieldReaderDetector detector) {
      this.fieldReaderDetectors.add(detector);
      return this;
    }

    @Override
    public Builder instanceBuilder(InstanceBuilderDetector detector) {
      this.instanceBuilderDetectors.add(detector);
      return this;
    }

    @Override
    public Builder classEncoding(ClassEncodingDetector detector) {
      this.classEncodingDetectors.add(detector);
      return this;
    }

    public Builder subTypes(SubTypesDetector detector) {
      this.subTypesDetectors.add(detector);
      return this;
    }

    @Override
    public Builder encodeValue(EncodeValueDetector detector) {
      this.encodeValueDetectors.add(detector);
      return this;
    }

    @Override
    public Builder decodeValue(DecodeValueDetector detector) {
      this.decodeValueDetectors.add(detector);
      return this;
    }

    @Override
    public Builder fieldName(FieldNameDetector detector) {
      this.fieldNameDetectors.add(detector);
      return this;
    }

    @Override
    public Builder flag(FlagDetector detector) {
      this.flagDetectors.add(detector);
      return this;
    }

    @Override
    public Builder typeName(TypeNameDetector detector) {
      this.typeNameDetectors.add(detector);
      return this;
    }

    @Override
    public Builder option(Option option) {
      this.options.add(option);
      return this;
    }

    @Override
    public Builder install(final Module module) {
      module.register(this);
      return this;
    }

    public Scribe build() {
      final Map<Class<? extends Option>, Option> options =
          this.options.stream().collect(Collectors.toMap(Option::getClass, Function.identity()));

      return new Scribe(Collections.unmodifiableList(new ArrayList<>(typeAliasDetectors)),
          Collections.unmodifiableList(new ArrayList<>(mappingDetectors)),
          Collections.unmodifiableList(new ArrayList<>(fieldReaderDetectors)),
          Collections.unmodifiableList(new ArrayList<>(instanceBuilderDetectors)),
          Collections.unmodifiableList(new ArrayList<>(classEncodingDetectors)),
          Collections.unmodifiableList(new ArrayList<>(subTypesDetectors)),
          Collections.unmodifiableList(new ArrayList<>(encodeValueDetectors)),
          Collections.unmodifiableList(new ArrayList<>(decodeValueDetectors)),
          Collections.unmodifiableList(new ArrayList<>(fieldNameDetectors)),
          Collections.unmodifiableList(new ArrayList<>(flagDetectors)),
          Collections.unmodifiableList(new ArrayList<>(typeNameDetectors)),
          Collections.unmodifiableMap(options));
    }
  }
}
