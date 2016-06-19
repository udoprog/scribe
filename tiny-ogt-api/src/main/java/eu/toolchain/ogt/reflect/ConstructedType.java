package eu.toolchain.ogt.reflect;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;

public interface ConstructedType {
    static ParameterizedConstructedType parameterized(
        final Class<?> rawType, final Type... typeArguments
    ) {
        return new ParameterizedConstructedType(rawType, typeArguments);
    }

    final class ParameterizedConstructedType implements ConstructedType, ParameterizedType {
        private final Class<?> rawType;
        private final Type[] typeArguments;

        ParameterizedConstructedType(
            Class<?> rawType, Type[] typeArguments
        ) {
            this.rawType = rawType;
            this.typeArguments = typeArguments;
        }

        @Override
        public Type[] getActualTypeArguments() {
            return typeArguments;
        }

        @Override
        public Type getRawType() {
            return rawType;
        }

        @Override
        public Type getOwnerType() {
            return null;
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(typeArguments) ^ rawType.hashCode();
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof ParameterizedType) {
                final ParameterizedType pt = (ParameterizedType) o;

                return pt.getOwnerType() == null && rawType.equals(pt.getRawType()) &&
                    Arrays.equals(typeArguments, pt.getActualTypeArguments());
            }

            return false;
        }
    }
}
