package eu.toolchain.ogt;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import eu.toolchain.ogt.binding.EntityBinding;
import eu.toolchain.ogt.creatormethod.CreatorField;
import eu.toolchain.ogt.creatormethod.CreatorMethod;
import eu.toolchain.ogt.entitymapper.BindingDetector;
import eu.toolchain.ogt.entitymapper.CreatorMethodDetector;
import eu.toolchain.ogt.entitymapper.FieldNameDetector;
import eu.toolchain.ogt.entitymapper.FieldReaderDetector;
import eu.toolchain.ogt.entitymapper.SubTypesDetector;
import eu.toolchain.ogt.entitymapper.TypeInterceptor;
import eu.toolchain.ogt.entitymapper.TypeNameDetector;
import eu.toolchain.ogt.entitymapper.ValueTypeDetector;
import eu.toolchain.ogt.fieldreader.FieldReader;
import eu.toolchain.ogt.subtype.EntitySubTypesProvider;
import eu.toolchain.ogt.type.AbstractEntityTypeMapping;
import eu.toolchain.ogt.type.ConcreteEntityTypeMapping;
import eu.toolchain.ogt.type.EntityTypeMapping;
import eu.toolchain.ogt.type.TypeMapping;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;
import java.util.stream.Stream;

@RequiredArgsConstructor
public class EntityMapper implements EntityResolver {
    private final List<FieldReaderDetector> fieldReaders;
    private final List<CreatorMethodDetector> creatorMethods;
    private final List<BindingDetector> bindings;
    private final List<SubTypesDetector> subTypeDetectors;
    private final List<ValueTypeDetector> valueTypeDetectors;
    private final List<FieldNameDetector> fieldNameDetectors;
    private final List<TypeNameDetector> typeNameDetectors;
    private final List<TypeInterceptor> typeInterceptors;

    private final ConcurrentMap<EntityKey, TypeMapping> cache = new ConcurrentHashMap<>();
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
     * @param type The type to resolve.
     * @return A Resolved PojoMapping for the given type.
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

            final TypeMapping newMapping = typeInterceptors
                .stream()
                .map(i -> i.intercept(this, type, annotations))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst()
                .orElseGet(() -> resolveBean(type));

            cache.put(key, newMapping);

            // lazily initialize to allow for circular dependencies.
            newMapping.initialize(this);
            return newMapping;
        }
    }

    @Override
    public TypeMapping mapping(final JavaType type) {
        return mapping(type, Annotations.empty());
    }

    @Override
    public Optional<CreatorMethod> detectCreatorMethod(JavaType type) {
        return firstMatch(creatorMethods.stream(), c -> c.detect(this, type));
    }

    @Override
    public Optional<FieldReader> detectFieldReader(
        final JavaType type, final String fieldName, final Optional<JavaType> knownType
    ) {
        return firstMatch(fieldReaders.stream(), c -> c.detect(type, fieldName, knownType));
    }

    @Override
    public Optional<EntityBinding> detectBinding(JavaType type) {
        return firstMatch(bindings.stream(), d -> d.detect(this, type));
    }

    public Map<String, EntityTypeMapping> resolveSubTypes(final JavaType type) {
        return firstMatch(subTypeDetectors.stream(), d -> d.detect(this, type))
            .map(EntitySubTypesProvider::subtypes)
            .orElseGet(ImmutableMap::of);
    }

    @Override
    public Optional<TypeMapping> detectValueType(final JavaType type) {
        return firstMatch(valueTypeDetectors.stream(), d -> d.detect(this, type));
    }

    @Override
    public Optional<String> detectFieldName(JavaType type, CreatorField field) {
        return firstMatch(Stream.concat(fieldNameDetectors.stream(), fieldNameDetectors.stream()),
            d -> d.detect(this, type, field));
    }

    @Override
    public Optional<String> detectName(JavaType type) {
        return firstMatch(typeNameDetectors.stream(), d -> d.detect(this, type));
    }

    @Override
    public List<CreatorField> setupCreatorFields(final JavaType type, final Executable executable) {
        final ImmutableList.Builder<CreatorField> fields = ImmutableList.builder();

        int index = 0;

        for (final Parameter p : executable.getParameters()) {
            final JavaType fieldType =
                JavaType.construct(executable.getGenericParameterTypes()[index++]);
            final Annotations annotations = Annotations.of(p.getAnnotations());

            fields.add(new CreatorField(annotations, Optional.of(fieldType), Optional.empty()));
        }

        return fields.build();
    }

    @Override
    public Annotations detectFieldAnnotations(final JavaType type, final String name) {
        final Field field;

        try {
            field = type.getRaw().getDeclaredField(name);
        } catch (final NoSuchFieldException e) {
            return Annotations.empty();
        }

        return Annotations.of(field.getAnnotations());
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

    private <T, O> Optional<O> firstMatch(Stream<T> alternatives, Function<T, Optional<O>> map) {
        return alternatives.map(map).filter(Optional::isPresent).map(Optional::get).findFirst();
    }

    private <O> TypeEncoding<Object, O> encodingFor(
        final EncodingFactory<O> factory, final JavaType type
    ) {
        final TypeMapping m = mapping(type);

        return new TypeEncoding<Object, O>() {
            @SuppressWarnings("unchecked")
            @Override
            public O encode(Object instance) {
                final TypeEncoder<O> typeEncoder = factory.fieldEncoder();
                return m.encode(typeEncoder, Context.ROOT, instance);
            }

            @SuppressWarnings("unchecked")
            @Override
            public Object decode(O instance) {
                final TypeDecoder<O> typeDecoder = factory.fieldDecoder();
                return m.decode(typeDecoder, Context.ROOT, instance);
            }

            @Override
            public TypeMapping mapping() {
                return m;
            }
        };
    }

    private EntityTypeMapping resolveBean(final JavaType type) {
        final Optional<String> typeName = detectName(type);

        if (type.isAbstract()) {
            return doAbstract(type, typeName);
        }

        return doConcrete(type, typeName);
    }

    private EntityTypeMapping doAbstract(
        final JavaType type, final Optional<String> typeName
    ) {
        final Map<String, EntityTypeMapping> subTypes = resolveSubTypes(type);

        final ImmutableMap.Builder<JavaType, EntityTypeMapping> subTypesByClass =
            ImmutableMap.builder();

        for (final Map.Entry<String, EntityTypeMapping> e : subTypes.entrySet()) {
            subTypesByClass.put(e.getValue().getType(), e.getValue());
        }

        return new AbstractEntityTypeMapping(type, typeName, subTypes, subTypesByClass.build());
    }

    private EntityTypeMapping doConcrete(final JavaType type, final Optional<String> typeName) {
        return new ConcreteEntityTypeMapping(this, type, typeName);
    }

    @Data
    public static class EntityKey {
        private final JavaType type;
        private final Annotations annotations;
    }

    public static class Builder implements EntityMapperBuilder<EntityMapper> {
        private ImmutableList.Builder<FieldReaderDetector> fieldReaders = ImmutableList.builder();
        private ImmutableList.Builder<CreatorMethodDetector> creatorMethods =
            ImmutableList.builder();
        private ImmutableList.Builder<BindingDetector> bindings = ImmutableList.builder();
        private ImmutableList.Builder<SubTypesDetector> subTypesDetectors = ImmutableList.builder();
        private ImmutableList.Builder<ValueTypeDetector> valueTypeDetectors =
            ImmutableList.builder();
        private ImmutableList.Builder<FieldNameDetector> fieldNameDetectors =
            ImmutableList.builder();
        private ImmutableList.Builder<TypeNameDetector> typeNameDetectors = ImmutableList.builder();
        private ImmutableList.Builder<TypeInterceptor> typeInterceptors = ImmutableList.builder();

        @Override
        public Builder fieldReaderDetector(FieldReaderDetector fieldReader) {
            this.fieldReaders.add(fieldReader);
            return this;
        }

        @Override
        public Builder creatorMethodDetector(CreatorMethodDetector creatorMethod) {
            this.creatorMethods.add(creatorMethod);
            return this;
        }

        @Override
        public Builder bindingDetector(BindingDetector binding) {
            this.bindings.add(binding);
            return this;
        }

        public Builder subTypesDetector(SubTypesDetector subTypeDetector) {
            this.subTypesDetectors.add(subTypeDetector);
            return this;
        }

        @Override
        public Builder valueTypeDetector(ValueTypeDetector valueTypeDetector) {
            this.valueTypeDetectors.add(valueTypeDetector);
            return this;
        }

        @Override
        public Builder fieldNameDetector(FieldNameDetector fieldNameDetector) {
            this.fieldNameDetectors.add(fieldNameDetector);
            return this;
        }

        @Override
        public Builder typeNameDetector(TypeNameDetector typeNameDetector) {
            this.typeNameDetectors.add(typeNameDetector);
            return this;
        }

        @Override
        public Builder typeInterceptor(TypeInterceptor typeInterceptor) {
            this.typeInterceptors.add(typeInterceptor);
            return this;
        }

        @Override
        public EntityMapper build() {
            return new EntityMapper(fieldReaders.build(), creatorMethods.build(), bindings.build(),
                subTypesDetectors.build(), valueTypeDetectors.build(), fieldNameDetectors.build(),
                typeNameDetectors.build(), typeInterceptors.build());
        }

        @Override
        public EntityMapperBuilder<EntityMapper> register(final Module module) {
            return module.register(this);
        }
    }
}
