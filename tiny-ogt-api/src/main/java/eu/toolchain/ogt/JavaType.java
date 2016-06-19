package eu.toolchain.ogt;

import lombok.Data;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface JavaType {
    Class asClass();

    static JavaType of(final TypeReference<?> reference) {
        return of(reference.getType(), Optional.empty());
    }

    static JavaType of(final Type type) {
        return of(type, Optional.empty());
    }

    static JavaType of(final Type type, final Optional<ParameterizedType> parent) {
        if (type instanceof java.lang.Class<?>) {
            return new Class((java.lang.Class<?>) type, parent);
        }

        if (type instanceof java.lang.reflect.ParameterizedType) {
            final java.lang.reflect.ParameterizedType pt =
                (java.lang.reflect.ParameterizedType) type;
            return new ParameterizedType(pt, parent);
        }

        if (type instanceof java.lang.reflect.TypeVariable<?>) {
            final ParameterizedType p = parent.orElseThrow(() -> new IllegalArgumentException(
                "Parent is required for type variables (" + type + ")"));
            return new TypeVariable((java.lang.reflect.TypeVariable<?>) type, p);
        }

        throw new IllegalStateException("Unsupported type: " + type);
    }

    @Data
    class TypeVariable implements JavaType {
        private final java.lang.reflect.TypeVariable<?> type;
        private final ParameterizedType parent;

        @Override
        public Class asClass() {
            return parent.resolve(type).asClass();
        }
    }

    @Data
    class Class implements JavaType {
        private final java.lang.Class<?> type;
        private final Optional<ParameterizedType> parent;

        @Override
        public Class asClass() {
            return this;
        }

        public List<JavaType.TypeVariable> getTypeParameters() {
            return parent
                .map(p -> Arrays
                    .stream(type.getTypeParameters())
                    .map(v -> new TypeVariable(v, p))
                    .collect(Collectors.toList()))
                .orElseGet(Collections::emptyList);
        }

        public Optional<JavaType> getTypeParameter(final int index) {
            final java.lang.reflect.TypeVariable<?>[] variables = type.getTypeParameters();

            if (index >= variables.length) {
                return Optional.empty();
            }

            return parent.map(p -> new TypeVariable(variables[index], p));
        }

        public Optional<Field> getField(final String field) {
            final java.lang.reflect.Field f;

            try {
                f = type.getDeclaredField(field);
            } catch (NoSuchFieldException e) {
                return Optional.empty();
            }

            return Optional.of(new Field(f, parent));
        }

        public Stream<Field> getFields() {
            return Arrays.stream(type.getDeclaredFields()).map(f -> new Field(f, parent));
        }
    }

    @Data
    class Field implements JavaType {
        private final java.lang.reflect.Field type;
        private final Optional<ParameterizedType> parent;

        @Override
        public Class asClass() {
            return getFieldType().asClass();
        }

        public JavaType getFieldType() {
            return of(type.getGenericType(), parent);
        }
    }

    @Data
    class ParameterizedType implements JavaType {
        private final java.lang.reflect.ParameterizedType type;
        private final Optional<ParameterizedType> parent;

        @Override
        public Class asClass() {
            return of(type.getRawType(), Optional.of(this)).asClass();
        }

        public JavaType resolve(final java.lang.reflect.TypeVariable<?> variable) {
            int index = 0;

            for (final java.lang.reflect.TypeVariable<?> v : ((java.lang.Class<?>) type
                .getRawType())
                .getTypeParameters()) {

                if (variable.equals(v)) {
                    return of(type.getActualTypeArguments()[index], Optional.of(this));
                }

                index++;
            }

            final ParameterizedType p = parent.orElseThrow(
                () -> new IllegalStateException("Unable to resolve variable: " + variable));
            return p.resolve(variable);
        }
    }
}
