package eu.toolchain.ogt;

import lombok.Data;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface TypeMatcher {
    Map<Type, Type> FORWARD_PRIMITIVES = forwardPrimitives();
    Map<Type, Type> BACKWARD_PRIMITIVES = backwardPrimitives();

    Set<Type> PRIMITIVES = Collections.unmodifiableSet(forwardPrimitives()
        .entrySet()
        .stream()
        .flatMap(e -> Stream.of(e.getKey(), e.getValue()))
        .collect(Collectors.toSet()));

    static Map<Type, Type> forwardPrimitives() {
        final Map<Type, Type> forward = new HashMap<>();

        forward.put(boolean.class, Boolean.class);
        forward.put(byte.class, Byte.class);
        forward.put(char.class, Character.class);
        forward.put(short.class, Short.class);
        forward.put(int.class, Integer.class);
        forward.put(long.class, Long.class);
        forward.put(float.class, Float.class);
        forward.put(double.class, Double.class);

        return Collections.unmodifiableMap(forward);
    }

    static Map<Type, Type> backwardPrimitives() {
        return Collections.unmodifiableMap(forwardPrimitives()
            .entrySet()
            .stream()
            .collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey)));
    }

    static TypeMatcher isPrimitive(Class<?> primitiveType) {
        final Type expect = FORWARD_PRIMITIVES.getOrDefault(primitiveType, primitiveType);
        return type -> FORWARD_PRIMITIVES.getOrDefault(type, type).equals(expect);
    }

    static TypeMatcher isPrimitive() {
        return PRIMITIVES::contains;
    }

    boolean matches(final Type type);

    static TypeMatcher isArray() {
        return type -> type instanceof Class<?> && ((Class<?>) type).isArray();
    }

    static TypeMatcher inPackage(final String packageName) {
        return new InPackage(packageName);
    }

    static TypeMatcher any() {
        return new Any();
    }

    static TypeMatcher upper(final TypeMatcher... matchers) {
        return new Upper(matchers);
    }

    static TypeMatcher lower(final TypeMatcher... matchers) {
        return new Lower(matchers);
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
    class InPackage implements TypeMatcher {
        private final String packageName;

        @Override
        public boolean matches(final Type type) {
            if (!(type instanceof Class<?>)) {
                return false;
            }

            final Class<?> c = (Class<?>) type;
            return c.getPackage().getName().startsWith(packageName + ".");
        }
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
