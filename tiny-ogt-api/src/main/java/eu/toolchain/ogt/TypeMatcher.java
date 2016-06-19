package eu.toolchain.ogt;

import lombok.Data;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;

public interface TypeMatcher {
    boolean matches(final Type type);

    static TypeMatcher any() {
        return new Any();
    }

    static TypeMatcher upper(final TypeMatcher... matchers) {
        return new Upper(matchers);
    }

    static TypeMatcher lower(final TypeMatcher... matchers) {
        return new Lower(matchers);
    }

    static TypeMatcher exact(final Class<?> base) {
        if (base.getTypeParameters().length != 0) {
            throw new IllegalArgumentException("Number of type arguments for class " + base + " (" +
                base.getTypeParameters().length + ") is not the expected (0)");
        }

        return new Exact(base);
    }

    static TypeMatcher instance(final Class<?> base) {
        return type -> type instanceof Class<?> && base.isAssignableFrom((Class<?>) type);
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
    class Any implements TypeMatcher {
        @Override
        public boolean matches(final Type type) {
            return true;
        }
    }

    @Data
    class Upper implements TypeMatcher {
        private final TypeMatcher[] matchers;

        @Override
        public boolean matches(final Type type) {
            if (type instanceof WildcardType) {
                final WildcardType wt = (WildcardType) type;

                return wt.getUpperBounds().length == matchers.length &&
                    matchers[0].matches(wt.getUpperBounds()[0]);
            }

            return false;
        }
    }

    @Data
    class Lower implements TypeMatcher {
        private final TypeMatcher[] matchers;

        @Override
        public boolean matches(final Type type) {
            if (type instanceof WildcardType) {
                final WildcardType wt = (WildcardType) type;

                return wt.getLowerBounds().length == matchers.length &&
                    matchers[0].matches(wt.getLowerBounds()[0]);
            }

            return false;
        }
    }

    @Data
    class Exact implements TypeMatcher {
        private final Type base;

        @Override
        public boolean matches(final Type type) {
            return base.equals(type);
        }
    }

    @Data
    class Parameterized implements TypeMatcher {
        private final Type base;
        private final TypeMatcher[] matchers;

        @Override
        public boolean matches(final Type type) {
            if (!(type instanceof ParameterizedType)) {
                return false;
            }

            final ParameterizedType pt = (ParameterizedType) type;
            final Type raw = pt.getRawType();

            if (!base.equals(raw)) {
                return false;
            }

            final Type[] types = pt.getActualTypeArguments();

            for (int i = 0; i < matchers.length; i++) {
                if (!matchers[i].matches(types[i])) {
                    return false;
                }
            }

            return true;
        }
    }
}
