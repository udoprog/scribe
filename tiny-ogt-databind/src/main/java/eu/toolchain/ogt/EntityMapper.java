package eu.toolchain.ogt;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import java.lang.reflect.Executable;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

import eu.toolchain.ogt.annotations.Bytes;
import eu.toolchain.ogt.annotations.Indexed;
import eu.toolchain.ogt.annotations.Kind;
import eu.toolchain.ogt.annotations.Parent;
import eu.toolchain.ogt.binding.Binding;
import eu.toolchain.ogt.creatormethod.CreatorField;
import eu.toolchain.ogt.creatormethod.CreatorMethod;
import eu.toolchain.ogt.entitymapper.BindingDetector;
import eu.toolchain.ogt.entitymapper.CreatorMethodDetector;
import eu.toolchain.ogt.entitymapper.FieldReaderDetector;
import eu.toolchain.ogt.entitymapper.NameDetector;
import eu.toolchain.ogt.entitymapper.PropertyNameDetector;
import eu.toolchain.ogt.entitymapper.SubTypesDetector;
import eu.toolchain.ogt.entitymapper.ValueTypeDetector;
import eu.toolchain.ogt.fieldreader.FieldReader;
import eu.toolchain.ogt.type.AbstractEntityTypeMapping;
import eu.toolchain.ogt.type.BytesTypeMapping;
import eu.toolchain.ogt.type.ConcreteEntityTypeMapping;
import eu.toolchain.ogt.type.DateMapping;
import eu.toolchain.ogt.type.EncodedBytesTypeMapping;
import eu.toolchain.ogt.type.EntityTypeMapping;
import eu.toolchain.ogt.type.ListTypeMapping;
import eu.toolchain.ogt.type.MapTypeMapping;
import eu.toolchain.ogt.type.OptionalTypeMapping;
import eu.toolchain.ogt.type.PrimitiveTypeMapping;
import eu.toolchain.ogt.type.StringTypeMapping;
import eu.toolchain.ogt.type.TypeMapping;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor
public class EntityMapper implements EntityResolver {
    public static final List<FieldReaderDetector> DEFAULT_FIELD_READERS = new ArrayList<>();

    private final List<FieldReaderDetector> fieldReaders;
    private final List<CreatorMethodDetector> creatorMethods;
    private final List<BindingDetector> bindings;
    private final List<SubTypesDetector> subTypeDetectors;
    private final List<ValueTypeDetector> valueTypeDetectors;
    private final List<PropertyNameDetector> propertyNameDetectors;
    private final List<NameDetector> nameDetectors;

    private final ConcurrentMap<JavaType, EntityTypeMapping> cache = new ConcurrentHashMap<>();
    private final Object resolverLock = new Object();

    public <O> TypeEncodingProvider<O> providerFor(final EncodingFactory<O> factory) {
        return new TypeEncodingProvider<O>() {
            @Override
            public TypeEncoding<Object, O> encodingFor(JavaType type) {
                return EntityMapper.this.encodingFor(factory, type);
            }

            @SuppressWarnings("unchecked")
            @Override
            public <T> TypeEncoding<T, O> encodingFor(Class<T> type) {
                return (TypeEncoding<T, O>) EntityMapper.this.encodingFor(factory,
                        JavaType.construct(type));
            }
        };
    }

    @Override
    public EntityTypeMapping mapping(Class<?> input) {
        return mapping(JavaType.construct(input));
    }

    @Override
    public TypeMapping resolveType(final JavaType t) {
        final Optional<PrimitiveType> primitive = PrimitiveType.detect(t);

        if (primitive.isPresent()) {
            return new PrimitiveTypeMapping(t, primitive.get());
        }

        /* Detect @JsonCreator and @JsonValue annotation builders */
        final Optional<TypeMapping> jsonCreator = detectValueType(t);

        if (jsonCreator.isPresent()) {
            return jsonCreator.get();
        }

        if (byte[].class.isAssignableFrom(t.getRawClass())) {
            return new BytesTypeMapping();
        }

        if (List.class.isAssignableFrom(t.getRawClass()) && t.getParameterCount() == 1) {
            return new ListTypeMapping(t, resolveType(t.getContainedType(0)));
        }

        if (Map.class.isAssignableFrom(t.getRawClass()) && t.getParameterCount() == 2) {
            return new MapTypeMapping(t, resolveType(t.getContainedType(0)),
                    resolveType(t.getContainedType(1)));
        }

        if (Optional.class.isAssignableFrom(t.getRawClass())) {
            return new OptionalTypeMapping(t, resolveType(t.getContainedType(0)));
        }

        if (String.class.isAssignableFrom(t.getRawClass())) {
            return new StringTypeMapping();
        }

        if (Date.class.isAssignableFrom(t.getRawClass())) {
            return new DateMapping();
        }

        /* assume complex entity */
        return mapping(t);
    }

    @Override
    public Optional<CreatorMethod> detectCreatorMethod(JavaType type) {
        return firstMatch(creatorMethods, c -> c.detect(this, type));
    }

    @Override
    public Optional<FieldReader> detectFieldReader(final JavaType type, final JavaType returnType,
            final String fieldName) {
        return firstMatch(fieldReaders, c -> c.detect(type, returnType, fieldName));
    }

    @Override
    public Optional<Binding> detectBinding(JavaType type) {
        return firstMatch(bindings, d -> d.detect(this, type));
    }

    public Map<String, EntityTypeMapping> resolveSubTypes(final JavaType type) {
        return firstMatch(subTypeDetectors, d -> d.detect(this, type)).map(s -> s.subtypes())
                .orElseGet(ImmutableMap::of);
    }

    public Optional<TypeMapping> detectValueType(final JavaType type) {
        return firstMatch(valueTypeDetectors, d -> d.detect(this, type));
    }

    @Override
    public Optional<String> detectPropertyName(JavaType type, CreatorField field) {
        return firstMatch(propertyNameDetectors, d -> d.detect(this, type, field));
    }

    @Override
    public Optional<String> detectName(JavaType type) {
        return firstMatch(nameDetectors, d -> d.detect(this, type));
    }

    @Override
    public List<CreatorField> setupCreatorFields(final Executable executable) {
        final ImmutableList.Builder<CreatorField> fields = ImmutableList.builder();

        int index = 0;

        for (final Parameter p : executable.getParameters()) {
            final JavaType fieldType =
                    JavaType.construct(executable.getGenericParameterTypes()[index++]);

            final boolean indexed = p.isAnnotationPresent(Indexed.class);
            final boolean bytes = p.isAnnotationPresent(Bytes.class);

            final TypeMapping mapping;

            if (bytes) {
                mapping = new EncodedBytesTypeMapping(fieldType);
            } else {
                mapping = resolveType(fieldType);
            }

            fields.add(new EntityMapperCreatorField(indexed, fieldType, mapping, p));
        }

        return fields.build();
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

    private <T, O> Optional<O> firstMatch(List<T> alternatives, Function<T, Optional<O>> map) {
        return alternatives.stream().map(map).filter(Optional::isPresent).map(Optional::get)
                .findFirst();
    }

    private <O> TypeEncoding<Object, O> encodingFor(final EncodingFactory<O> factory,
            final JavaType type) {
        final TypeMapping m = resolveType(type);

        return new TypeEncoding<Object, O>() {
            @SuppressWarnings("unchecked")
            @Override
            public O encode(Object instance) {
                final FieldEncoder fieldEncoder = factory.fieldEncoder();
                return (O) fieldEncoder.filter(m.encode(fieldEncoder, Context.ROOT, instance));
            }

            @Override
            public Object decode(O instance) {
                final FieldDecoder fieldDecoder = factory.fieldDecoder(instance);
                return m.decode(fieldDecoder, Context.ROOT);
            }

            @Override
            public TypeMapping mapping() {
                return m;
            }
        };
    }

    /**
     * Performed a cached resolve of the given type.
     *
     * This will put the resolved type into {@link #cache} before they are being initialized to
     * allow for circular dependencies.
     *
     * @param pojo The type to resolve.
     * @return A Resolved PojoMapping for the given type.
     */
    private EntityTypeMapping mapping(final JavaType pojo) {
        final EntityTypeMapping mapping = cache.get(pojo);

        if (mapping != null) {
            return mapping;
        }

        synchronized (resolverLock) {
            final EntityTypeMapping candidate = cache.get(pojo);

            if (candidate != null) {
                return candidate;
            }

            final EntityTypeMapping newMapping = resolveBean(pojo);
            cache.put(pojo, newMapping);

            // lazily initialize to allow for circular dependencies.
            newMapping.initialize(this);
            return newMapping;
        }
    }

    private EntityTypeMapping resolveBean(final JavaType type) {
        final Optional<String> typeName = detectName(type);

        final Map<String, EntityTypeMapping> subTypes = resolveSubTypes(type);

        final Class<?> raw = type.getRawClass();

        if (Reflection.isAbstract(raw)) {
            return doAbstract(type, typeName, subTypes);
        }

        return doConcrete(type, typeName, raw);
    }

    private <T> EntityTypeMapping doAbstract(final JavaType type, final Optional<String> typeName,
            final Map<String, EntityTypeMapping> subTypes) {
        final ImmutableMap.Builder<JavaType, EntityTypeMapping> subTypesByClass =
                ImmutableMap.builder();

        for (final Map.Entry<String, EntityTypeMapping> e : subTypes.entrySet()) {
            subTypesByClass.put(e.getValue().getType(), e.getValue());
        }

        final TypeKey key = entityKey(type);
        return new AbstractEntityTypeMapping<T>(type, key, typeName, subTypes,
                subTypesByClass.build());
    }

    private EntityTypeMapping doConcrete(final JavaType type, final Optional<String> typeName,
            final Class<?> raw) {
        final TypeKey key = entityKey(type);
        return new ConcreteEntityTypeMapping(this, type, key, typeName);
    }

    private TypeKey entityKey(final JavaType type) {
        final String kind = Optional.ofNullable(type.getRawClass().getAnnotation(Kind.class))
                .map(Kind::value).filter(v -> !"".equals(v))
                .orElseGet(() -> type.getRawClass().getCanonicalName());

        final Optional<TypeKey> parent =
                Optional.ofNullable(type.getRawClass().getAnnotation(Parent.class))
                        .map(a -> mapping(JavaType.construct(a.value())).key());

        return new TypeKey(kind, parent);
    }

    @RequiredArgsConstructor
    @ToString
    static class EntityMapperCreatorField implements CreatorField {
        private final boolean indexed;
        private final JavaType type;
        private final TypeMapping mapping;
        private final Parameter parameter;

        @Override
        public boolean indexed() {
            return indexed;
        }

        @Override
        public JavaType type() {
            return type;
        }

        @Override
        public TypeMapping mapping() {
            return mapping;
        }

        @Override
        public Parameter parameter() {
            return parameter;
        }
    }

    public static class Builder implements EntityMapperBuilder<EntityMapper> {
        private List<FieldReaderDetector> fieldReaders = ImmutableList.of();
        private List<CreatorMethodDetector> creatorMethods = ImmutableList.of();
        private List<BindingDetector> bindings = ImmutableList.of();
        private List<SubTypesDetector> subTypesDetectors = ImmutableList.of();
        private List<ValueTypeDetector> valueTypeDetectors = ImmutableList.of();
        private List<PropertyNameDetector> propertyNameDetectors = ImmutableList.of();
        private List<NameDetector> nameDetectors = ImmutableList.of();

        @Override
        public Builder registerFieldReader(FieldReaderDetector fieldReader) {
            this.fieldReaders = copyAndAdd(fieldReaders, fieldReader);
            return this;
        }

        @Override
        public Builder registerCreatorMethod(CreatorMethodDetector creatorMethod) {
            this.creatorMethods = copyAndAdd(creatorMethods, creatorMethod);
            return this;
        }

        @Override
        public Builder registerBinding(BindingDetector binding) {
            this.bindings = copyAndAdd(bindings, binding);
            return this;
        }

        public Builder registerSubTypes(SubTypesDetector subTypeDetector) {
            this.subTypesDetectors = copyAndAdd(subTypesDetectors, subTypeDetector);
            return this;
        }

        @Override
        public Builder registerValueType(ValueTypeDetector valueTypeDetector) {
            this.valueTypeDetectors = copyAndAdd(valueTypeDetectors, valueTypeDetector);
            return this;
        }

        @Override
        public Builder registerPropertyNameDetector(PropertyNameDetector propertyNameDetector) {
            this.propertyNameDetectors = copyAndAdd(propertyNameDetectors, propertyNameDetector);
            return this;
        }

        @Override
        public Builder registerNameDetector(NameDetector nameDetector) {
            this.nameDetectors = copyAndAdd(nameDetectors, nameDetector);
            return this;
        }

        @Override
        public EntityMapper build() {
            return new EntityMapper(fieldReaders, creatorMethods, bindings, subTypesDetectors,
                    valueTypeDetectors, propertyNameDetectors, nameDetectors);
        }

        @Override
        public EntityMapperBuilder<EntityMapper> register(final Module module) {
            return module.register(this);
        }

        private <T> List<T> copyAndAdd(List<T> original, T addition) {
            return ImmutableList.<T> builder().addAll(original).add(addition).build();
        }
    }
}
