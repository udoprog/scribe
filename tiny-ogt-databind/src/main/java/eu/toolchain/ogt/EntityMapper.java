package eu.toolchain.ogt;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Ordering;
import eu.toolchain.ogt.creatormethod.CreatorMethod;
import eu.toolchain.ogt.entitybinding.EntityBinding;
import eu.toolchain.ogt.entitymapper.BindingDetector;
import eu.toolchain.ogt.entitymapper.CreatorMethodDetector;
import eu.toolchain.ogt.entitymapper.FieldNameDetector;
import eu.toolchain.ogt.entitymapper.FieldReaderDetector;
import eu.toolchain.ogt.entitymapper.SubTypesDetector;
import eu.toolchain.ogt.entitymapper.TypeNameDetector;
import eu.toolchain.ogt.entitymapper.ValueTypeDetector;
import eu.toolchain.ogt.fieldreader.FieldReader;
import eu.toolchain.ogt.typemapper.TypeMapper;
import eu.toolchain.ogt.typemapping.AbstractEntityTypeMapping;
import eu.toolchain.ogt.typemapping.ConcreteEntityTypeMapping;
import eu.toolchain.ogt.typemapping.EntityTypeMapping;
import eu.toolchain.ogt.typemapping.TypeMapping;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.Executable;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
public class EntityMapper implements EntityResolver {
    private final List<TypeMapper> typeMappers;
    private final List<FieldReaderDetector> fieldReaders;
    private final List<CreatorMethodDetector> creatorMethods;
    private final List<BindingDetector> bindings;
    private final List<SubTypesDetector> subTypeDetectors;
    private final List<ValueTypeDetector> valueTypeDetectors;
    private final List<FieldNameDetector> fieldNameDetectors;
    private final List<TypeNameDetector> typeNameDetectors;

    private final ConcurrentMap<EntityKey, TypeMapping> cache = new ConcurrentHashMap<>();
    private final Object resolverLock = new Object();

    public <Target> TypeEncodingProvider<Target> providerFor(
        final EncodingFactory<Target> factory
    ) {
        return new TypeEncodingProvider<Target>() {
            @Override
            public Encoder<Target, Object> newEncoder(Type type) {
                return mapping(type)
                    .newEncoder(EntityMapper.this, factory)
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

            @Override
            public Decoder<Target, Object> newDecoder(Type type) {
                return mapping(type)
                    .newDecoder(EntityMapper.this, factory)
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
     * Performed a cached resolve of the given type.
     * <p>
     * This will put the resolved type into {@link #cache} before they are being initialized to
     * allow for circular dependencies.
     *
     * @param type The type to resolve.
     * @return A Resolved PojoMapping for the given type.
     */
    @Override
    public TypeMapping mapping(final Type type, final Annotations annotations) {
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

            final TypeMapping newMapping =
                firstMatch(typeMappers.stream(), m -> m.map(this, type)).orElseGet(
                    () -> resolveBean(type));

            cache.put(key, newMapping);
            newMapping.initialize(this);
            return newMapping;
        }
    }

    @Override
    public TypeMapping mapping(final Type type) {
        return mapping(type, Annotations.empty());
    }

    @Override
    public Optional<CreatorMethod> detectCreatorMethod(Type type) {
        return firstPriorityMatch(creatorMethods.stream(), c -> c.detect(this, type));
    }

    @Override
    public Optional<FieldReader> detectFieldReader(
        final Type type, final String fieldName, final Optional<Type> knownType
    ) {
        return firstPriorityMatch(fieldReaders.stream(), c -> c.detect(type, fieldName, knownType));
    }

    @Override
    public Optional<EntityBinding> detectBinding(Type type) {
        return firstPriorityMatch(bindings.stream(), d -> d.detect(this, type));
    }

    public List<EntityTypeMapping> resolveSubTypes(final Type type) {
        return firstPriorityMatch(subTypeDetectors.stream(), d -> d.detect(this, type)).orElseGet(
            ImmutableList::of);
    }

    @Override
    public Optional<TypeMapping> detectValueType(final Type type) {
        return firstPriorityMatch(valueTypeDetectors.stream(), d -> d.detect(this, type));
    }

    @Override
    public Optional<String> detectFieldName(Type type, Annotations annotations) {
        return firstPriorityMatch(fieldNameDetectors.stream(),
            d -> d.detect(this, type, annotations));
    }

    @Override
    public Optional<String> detectName(Type type) {
        return firstPriorityMatch(typeNameDetectors.stream(), d -> d.detect(this, type));
    }

    @Override
    public List<EntityField> detectExecutableFields(final Executable executable) {
        final ImmutableList.Builder<EntityField> fields = ImmutableList.builder();

        int index = 0;

        for (final Parameter p : executable.getParameters()) {
            final int i = index++;

            final Type fieldType = executable.getGenericParameterTypes()[i];
            final Annotations annotations = Annotations.of(p.getAnnotations());

            fields.add(new EntityField(i, annotations, Optional.of(fieldType), Optional.empty()));
        }

        return fields.build();
    }

    @Override
    public Annotations detectFieldAnnotations(final Type type, final String name) {
        if (!(type instanceof Class<?>)) {
            return Annotations.empty();
        }

        final Class<?> c = (Class<?>) type;

        final java.lang.reflect.Field field;

        try {
            field = c.getDeclaredField(name);
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

    private static final Ordering<Match<?>> BY_SCORE = Ordering.<Match<?>>from(
        (a, b) -> Integer.compare(b.getPriority().ordinal(), a.getPriority().ordinal()));

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

        final List<Match<Source>> sorted = BY_SCORE.sortedCopy(results);

        if (results.size() > 1) {
            if (sorted.get(0).getPriority() == sorted.get(1).getPriority()) {
                throw new IllegalArgumentException(
                    "Found multiple matches with the same priority: " + sorted);
            }
        }

        return sorted.stream().map(Match::getValue).findFirst();
    }

    private EntityTypeMapping resolveBean(final Type type) {
        final Optional<String> typeName = detectName(type);

        final Class<?> c = Reflection
            .asClass(type)
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Not a class (" + type + ")"));

        if (Reflection.isAbstract(c)) {
            return doAbstract(type, typeName);
        }

        return doConcrete(type, typeName);
    }

    private EntityTypeMapping doAbstract(
        final Type type, final Optional<String> typeName
    ) {
        final List<EntityTypeMapping> subTypes = resolveSubTypes(type);

        return new AbstractEntityTypeMapping(type, typeName, subTypes);
    }

    private EntityTypeMapping doConcrete(final Type type, final Optional<String> typeName) {
        return new ConcreteEntityTypeMapping(type, typeName);
    }

    @Data
    public static class EntityKey {
        private final Type type;
        private final Annotations annotations;
    }

    public static class Builder implements EntityMapperBuilder<EntityMapper> {
        private ImmutableList.Builder<TypeMapper> typeMappers = ImmutableList.builder();
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

        @Override
        public Builder typeMapper(TypeMapper typeMapper) {
            this.typeMappers.add(typeMapper);
            return this;
        }

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
        public EntityMapper build() {
            return new EntityMapper(typeMappers.build(), fieldReaders.build(),
                creatorMethods.build(), bindings.build(), subTypesDetectors.build(),
                valueTypeDetectors.build(), fieldNameDetectors.build(), typeNameDetectors.build());
        }

        @Override
        public EntityMapperBuilder<EntityMapper> register(final Module module) {
            return module.register(this);
        }
    }
}
