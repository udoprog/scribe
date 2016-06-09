package eu.toolchain.ogt;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import eu.toolchain.ogt.binding.Binding;
import eu.toolchain.ogt.creatormethod.CreatorField;
import eu.toolchain.ogt.creatormethod.CreatorMethod;
import eu.toolchain.ogt.entitymapper.BindingDetector;
import eu.toolchain.ogt.entitymapper.CreatorMethodDetector;
import eu.toolchain.ogt.entitymapper.FieldReaderDetector;
import eu.toolchain.ogt.entitymapper.NameDetector;
import eu.toolchain.ogt.entitymapper.PropertyNameDetector;
import eu.toolchain.ogt.entitymapper.SubTypesDetector;
import eu.toolchain.ogt.entitymapper.TypeMappingInterceptor;
import eu.toolchain.ogt.entitymapper.ValueTypeDetector;
import eu.toolchain.ogt.fieldreader.FieldReader;
import eu.toolchain.ogt.subtype.EntitySubTypesProvider;
import eu.toolchain.ogt.type.AbstractEntityTypeMapping;
import eu.toolchain.ogt.type.BytesTypeMapping;
import eu.toolchain.ogt.type.ConcreteEntityTypeMapping;
import eu.toolchain.ogt.type.DateMapping;
import eu.toolchain.ogt.type.EntityTypeMapping;
import eu.toolchain.ogt.type.ListTypeMapping;
import eu.toolchain.ogt.type.MapTypeMapping;
import eu.toolchain.ogt.type.OptionalTypeMapping;
import eu.toolchain.ogt.type.PrimitiveTypeMapping;
import eu.toolchain.ogt.type.StringTypeMapping;
import eu.toolchain.ogt.type.TypeMapping;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.Executable;
import java.lang.reflect.Parameter;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

@RequiredArgsConstructor
public class EntityMapper implements EntityResolver {
    private final List<FieldReaderDetector> fieldReaders;
    private final List<CreatorMethodDetector> creatorMethods;
    private final List<BindingDetector> bindings;
    private final List<SubTypesDetector> subTypeDetectors;
    private final List<ValueTypeDetector> valueTypeDetectors;
    private final List<PropertyNameDetector> propertyNameDetectors;
    private final List<NameDetector> nameDetectors;
    private final List<TypeMappingInterceptor> typeMappingInterceptors;

    private final ConcurrentMap<JavaType, TypeMapping> cache = new ConcurrentHashMap<>();
    private final Object resolverLock = new Object();

    public <T> TypeEncodingProvider<T> providerFor(final EncodingFactory<T> factory) {
        return new TypeEncodingProvider<T>() {
            @Override
            public TypeEncoding<Object, T> encodingFor(JavaType type) {
                return EntityMapper.this.encodingFor(factory, type);
            }

            @Override
            public <O> TypeEncoding<O, T> encodingFor(Class<O> type) {
                return (TypeEncoding<O, T>) EntityMapper.this.encodingFor(factory,
                    JavaType.construct(type));
            }
        };
    }

    @Override
    public TypeMapping mapping(Class<?> input) {
        return mapping(JavaType.construct(input));
    }

    /**
     * Performed a cached resolve of the given type.
     * <p>
     * This will put the resolved type into {@link #cache} before they are being initialized to
     * allow for circular dependencies.
     *
     * @param input The type to resolve.
     * @return A Resolved PojoMapping for the given type.
     */
    @Override
    public TypeMapping mapping(final JavaType input) {
        final TypeMapping mapping = cache.get(input);

        if (mapping != null) {
            return mapping;
        }

        synchronized (resolverLock) {
            final TypeMapping candidate = cache.get(input);

            if (candidate != null) {
                return candidate;
            }

            final TypeMapping newMapping = resolveMapping(input);
            cache.put(input, newMapping);

            // lazily initialize to allow for circular dependencies.
            newMapping.initialize(this);
            return newMapping;
        }
    }

    @Override
    public TypeMapping mapping(final JavaType type, final Annotations annotations) {
        return typeMappingInterceptors
            .stream()
            .map(i -> i.intercept(this, type, annotations))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .findFirst()
            .orElseGet(() -> mapping(type));
    }

    private TypeMapping resolveMapping(final JavaType t) {
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
            return new ListTypeMapping(t, mapping(t.getContainedType(0)));
        }

        if (Map.class.isAssignableFrom(t.getRawClass()) && t.getParameterCount() == 2) {
            return new MapTypeMapping(t, mapping(t.getContainedType(0)),
                mapping(t.getContainedType(1)));
        }

        if (Optional.class.isAssignableFrom(t.getRawClass())) {
            return new OptionalTypeMapping(t, mapping(t.getContainedType(0)));
        }

        if (String.class.isAssignableFrom(t.getRawClass())) {
            return new StringTypeMapping();
        }

        if (Date.class.isAssignableFrom(t.getRawClass())) {
            return new DateMapping();
        }

        /* assume complex entity */
        return resolveBean(t);
    }

    @Override
    public Optional<CreatorMethod> detectCreatorMethod(JavaType type) {
        return firstMatch(creatorMethods, c -> c.detect(this, type));
    }

    @Override
    public Optional<FieldReader> detectFieldReader(
        final JavaType type, final String fieldName, final Optional<JavaType> knownType
    ) {
        return firstMatch(fieldReaders, c -> c.detect(type, fieldName, knownType));
    }

    @Override
    public Optional<Binding> detectBinding(JavaType type) {
        return firstMatch(bindings, d -> d.detect(this, type));
    }

    public Map<String, EntityTypeMapping> resolveSubTypes(final JavaType type) {
        return firstMatch(subTypeDetectors, d -> d.detect(this, type))
            .map(EntitySubTypesProvider::subtypes)
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
            final Annotations annotations = Annotations.of(p.getAnnotations());

            fields.add(setupCreatorField(annotations, Optional.of(fieldType), Optional.empty()));
        }

        return fields.build();
    }

    @Override
    public CreatorField setupCreatorField(
        final Annotations annotations, final Optional<JavaType> fieldType,
        final Optional<String> fieldName
    ) {
        return new CreatorField(annotations, fieldType, fieldName);
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
        return alternatives
            .stream()
            .map(map)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .findFirst();
    }

    private <O> TypeEncoding<Object, O> encodingFor(
        final EncodingFactory<O> factory, final JavaType type
    ) {
        final TypeMapping m = mapping(type);

        return new TypeEncoding<Object, O>() {
            @SuppressWarnings("unchecked")
            @Override
            public O encode(Object instance) {
                final FieldEncoder<O> fieldEncoder = factory.fieldEncoder();
                return m.encode(fieldEncoder, Context.ROOT, instance);
            }

            @SuppressWarnings("unchecked")
            @Override
            public Object decode(O instance) {
                final FieldDecoder<O> fieldDecoder = factory.fieldDecoder();
                return m.decode(fieldDecoder, Context.ROOT, instance);
            }

            @Override
            public TypeMapping mapping() {
                return m;
            }
        };
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

    private EntityTypeMapping doAbstract(
        final JavaType type, final Optional<String> typeName,
        final Map<String, EntityTypeMapping> subTypes
    ) {
        final ImmutableMap.Builder<JavaType, EntityTypeMapping> subTypesByClass =
            ImmutableMap.builder();

        for (final Map.Entry<String, EntityTypeMapping> e : subTypes.entrySet()) {
            subTypesByClass.put(e.getValue().getType(), e.getValue());
        }

        return new AbstractEntityTypeMapping(type, typeName, subTypes, subTypesByClass.build());
    }

    private EntityTypeMapping doConcrete(
        final JavaType type, final Optional<String> typeName, final Class<?> raw
    ) {
        return new ConcreteEntityTypeMapping(this, type, typeName);
    }

    public static class Builder implements EntityMapperBuilder<EntityMapper> {
        private List<FieldReaderDetector> fieldReaders = ImmutableList.of();
        private List<CreatorMethodDetector> creatorMethods = ImmutableList.of();
        private List<BindingDetector> bindings = ImmutableList.of();
        private List<SubTypesDetector> subTypesDetectors = ImmutableList.of();
        private List<ValueTypeDetector> valueTypeDetectors = ImmutableList.of();
        private List<PropertyNameDetector> propertyNameDetectors = ImmutableList.of();
        private List<NameDetector> nameDetectors = ImmutableList.of();
        private List<TypeMappingInterceptor> typeMappingInterceptors = ImmutableList.of();

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
        public Builder registerTypeMappingInterceptor(
            TypeMappingInterceptor typeMappingInterceptor
        ) {
            this.typeMappingInterceptors =
                copyAndAdd(typeMappingInterceptors, typeMappingInterceptor);
            return this;
        }

        @Override
        public EntityMapper build() {
            return new EntityMapper(fieldReaders, creatorMethods, bindings, subTypesDetectors,
                valueTypeDetectors, propertyNameDetectors, nameDetectors, typeMappingInterceptors);
        }

        @Override
        public EntityMapperBuilder<EntityMapper> register(final Module module) {
            return module.register(this);
        }

        private <T> List<T> copyAndAdd(List<T> original, T addition) {
            return ImmutableList.<T>builder().addAll(original).add(addition).build();
        }
    }
}
