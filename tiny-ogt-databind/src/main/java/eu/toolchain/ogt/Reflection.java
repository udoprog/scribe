package eu.toolchain.ogt;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;

public abstract class Reflection {
    /**
     * Check if the given type is abstract through its modifiers.
     *
     * @param type The type to check.
     * @return {@code true} if the specified type is abstract.
     */
    public static boolean isAbstract(final Class<?> type) {
        return (type.getModifiers() & Modifier.INTERFACE) != 0
                || (type.getModifiers() & Modifier.ABSTRACT) != 0;
    }

    public static boolean isPublic(final Executable value) {
        return (value.getModifiers() & Modifier.PUBLIC) != 0;
    }

    public static boolean isStatic(final Executable value) {
        return (value.getModifiers() & Modifier.STATIC) != 0;
    }

    public static Stream<Constructor<?>> findAnnotatedConstructors(final JavaType type,
            final Class<? extends Annotation> a) {
        return Arrays.stream(type.getRawClass().getDeclaredConstructors())
                .filter(m -> m.isAnnotationPresent(a));
    }

    public static Stream<Method> findAnnotatedMethods(final JavaType type,
            final Class<? extends Annotation> a) {
        return Arrays.stream(type.getRawClass().getDeclaredMethods())
                .filter(m -> m.isAnnotationPresent(a));
    }

    @SuppressWarnings("unchecked")
    public static Optional<Class<? extends Annotation>> detectPresentAnnotation(
            final String canonicalName) {
        final Class<?> detected;

        try {
            detected = Class.forName(canonicalName);
        } catch (final ClassNotFoundException e) {
            return Optional.empty();
        }

        if (!detected.isAnnotation()) {
            throw new IllegalArgumentException("Not an annotation: " + canonicalName);
        }

        return Optional.of((Class<? extends Annotation>) detected);
    }
}
