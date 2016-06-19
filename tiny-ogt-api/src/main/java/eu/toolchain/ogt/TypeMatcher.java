package eu.toolchain.ogt;

import eu.toolchain.ogt.type.JavaType;
import lombok.Data;

import java.lang.reflect.Type;
import java.util.Arrays;

public interface TypeMatcher {
    static TypeMatcher isPrimitive(Class<?> primitiveType) {
        final JavaType primitive = JavaType.of(primitiveType);

        if (!primitive.isPrimitive()) {
            throw new IllegalArgumentException("Not a primitive type: " + primitive);
        }

        final JavaType expect = JavaType.PRIMITIVES_TO_BOXED.getOrDefault(primitive, primitive);
        return type -> JavaType.PRIMITIVES_TO_BOXED.getOrDefault(type, type).equals(expect);
    }

    static TypeMatcher isPrimitive() {
        return JavaType::isPrimitive;
    }

    boolean matches(final JavaType type);

    static TypeMatcher isArray() {
        return type -> type.getType().isArray();
    }

    static TypeMatcher inPackage(final String packageName) {
        return new InPackage(packageName);
    }

    static TypeMatcher any() {
        return new Any();
    }

    static TypeMatcher anyOf(final TypeMatcher... matchers) {
        return type -> Arrays.stream(matchers).filter(m -> m.matches(type)).findFirst().isPresent();
    }

    static TypeMatcher exact(final Class<?> base) {
        if (base.getTypeParameters().length != 0) {
            throw new IllegalArgumentException("Number of type arguments for class " + base + " (" +
                base.getTypeParameters().length + ") is not the expected (0)");
        }

        return new Exact(base);
    }

    static TypeMatcher instance(final Class<?> base) {
        return type -> base.isAssignableFrom(type.getType());
    }

    static TypeMatcher parameterized(final Class<?> base, final TypeMatcher... matchers) {
        if (base.getTypeParameters().length != matchers.length) {
            throw new IllegalArgumentException("Number of type arguments for class " + base + " (" +
                base.getTypeParameters().length + ") is not the expected (" +
                matchers.length + ")");
        }

        return new Parameterized(base, matchers);
    }

    @Data
    class InPackage implements TypeMatcher {
        private final String packageName;

        @Override
        public boolean matches(final JavaType type) {
            return type.getType().getPackage().getName().startsWith(packageName + ".");
        }
    }

    @Data
    class Any implements TypeMatcher {
        @Override
        public boolean matches(final JavaType type) {
            return true;
        }
    }

    @Data
    class Exact implements TypeMatcher {
        private final Type base;

        @Override
        public boolean matches(final JavaType type) {
            return base.equals(type.getType());
        }
    }

    @Data
    class Parameterized implements TypeMatcher {
        private final Class<?> base;
        private final TypeMatcher[] matchers;

        @Override
        public boolean matches(final JavaType type) {
            if (!type.getType().equals(base)) {
                return false;
            }

            int index = 0;

            for (final JavaType j : type.getTypeParameters()) {
                if (!matchers[index++].matches(j)) {
                    return false;
                }
            }

            return true;
        }
    }
}
