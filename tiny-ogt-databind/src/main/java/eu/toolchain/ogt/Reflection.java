package eu.toolchain.ogt;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Executable;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

public abstract class Reflection {
    public static boolean isPublic(final Executable value) {
        return (value.getModifiers() & Modifier.PUBLIC) != 0;
    }

    public static boolean isStatic(final Executable value) {
        return (value.getModifiers() & Modifier.STATIC) != 0;
    }

    public static Stream<Class<?>> asClass(final Type type) {
        if (type instanceof Class<?>) {
            return Stream.of((Class<?>) type);
        }

        if (type instanceof ParameterizedType) {
            return asClass(((ParameterizedType) type).getRawType());
        }

        return Stream.empty();
    }

    public static Optional<ParameterizedType> asParameterizedType(final Type type) {
        return (type instanceof ParameterizedType) ? Optional.of((ParameterizedType) type)
            : Optional.empty();
    }

    public static <A extends Annotation, E extends AnnotatedElement> Stream<E> findByAnnotation(
        final Type type, Class<A> a, Function<Class<?>, E[]> function
    ) {
        return asClass(type)
            .map(c -> Arrays.stream(function.apply(c)))
            .flatMap(c -> c.filter(m -> m.isAnnotationPresent(a)));
    }

    public static boolean isAbstract(final Type type) {
        return asClass(type)
            .filter(c -> (c.getModifiers() & (Modifier.ABSTRACT | Modifier.INTERFACE)) != 0)
            .findFirst()
            .isPresent();
    }

    public static <A extends Annotation> Stream<A> getAnnotation(
        final Type type, final Class<A> annotation
    ) {
        return asClass(type).map(c -> c.getAnnotation(annotation)).filter(a -> a != null);
    }
}
