package eu.toolchain.ogt.type;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import eu.toolchain.ogt.TypeReference;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
@EqualsAndHashCode(of = {"type", "typeParameters"})
public class JavaType implements AccessibleType, AnnotatedType {
    private static final Joiner PARAMETER_JOINER = Joiner.on(", ");

    /**
     * All unboxed primitive classes to their boxed equivalent, excluding void.
     */
    public static Map<JavaType, JavaType> PRIMITIVES_TO_BOXED = primitivesToBoxed();

    /**
     * All boxed primitive classes to their unboxed equivalent, excluding void.
     */
    public static Map<JavaType, JavaType> PRIMITIVES_TO_UNBOXED = primitivesToUnboxed();

    /**
     * A collection of all possible prinitive classes (boxed and unboxed), excluding void.
     */
    public static Set<JavaType> PRIMITIVES = Collections.unmodifiableSet(primitivesToBoxed()
        .entrySet()
        .stream()
        .flatMap(e -> Stream.of(e.getKey(), e.getValue()))
        .collect(Collectors.toSet()));

    private final Optional<TypeResolver> parent;
    private final java.lang.Class<?> type;
    private final List<JavaType> typeParameters;

    public boolean isPrimitive() {
        return PRIMITIVES.contains(this);
    }

    public boolean isBoolean() {
        return type == boolean.class || type == Boolean.class;
    }

    public boolean isVoid() {
        return type == void.class || type == Void.class;
    }

    public Optional<JavaType> getTypeParameter(final int index) {
        if (index < 0 || index >= typeParameters.size()) {
            return Optional.empty();
        }

        return Optional.of(typeParameters.get(index));
    }

    public Optional<Field> getField(final String field) {
        return getFields().filter(f -> f.getName().equals(field)).findFirst();
    }

    public Stream<Field> getFields() {
        return Arrays.stream(type.getDeclaredFields()).map(f -> {
            final JavaType fieldType = of(f.getGenericType(), parent);
            final List<Annotation> annotations = ImmutableList.copyOf(f.getAnnotations());
            return new Field(annotations, f.getModifiers(), fieldType, f.getName());
        });
    }

    public Stream<Method> getMethod(final String name, final JavaType... parameterTypes) {
        final List<JavaType> parameters = ImmutableList.copyOf(parameterTypes);

        return getMethods()
            .filter(m -> m.getName().equals(name))
            .filter(m -> m
                .getParameters()
                .stream()
                .map(Parameter::getParameterType)
                .collect(Collectors.toList())
                .equals(parameters));
    }

    public Stream<Method> getMethods() {
        return Arrays.stream(type.getDeclaredMethods()).map(m -> {
            final JavaType returnType = of(m.getGenericReturnType(), parent);
            final List<Parameter> parameters = buildParameters(m, parent);
            final List<Annotation> annotations = ImmutableList.copyOf(m.getAnnotations());
            return new Method(m, annotations, m.getModifiers(), returnType, m.getName(),
                parameters);
        });
    }

    public Stream<Constructor> getConstructors() {
        return Arrays.stream(type.getDeclaredConstructors()).map(m -> {
            final List<Parameter> parameters = buildParameters(m, parent);
            return new Constructor(m, parameters, ImmutableList.copyOf(m.getAnnotations()),
                m.getModifiers());
        });
    }

    @Override
    public int getModifiers() {
        return type.getModifiers();
    }

    @Override
    public Stream<Annotation> getAnnotations() {
        return Arrays.stream(type.getAnnotations());
    }

    public <E extends AnnotatedType, A extends Annotation> Stream<E> findByAnnotation(
        Function<JavaType, Stream<E>> function, Class<A> annotation
    ) {
        return function.apply(this).filter(m -> m.isAnnotationPresent(annotation));
    }

    @Override
    public String toString() {
        if (typeParameters.size() == 0) {
            return type.getCanonicalName();
        }

        return type.getCanonicalName() + "<" +
            PARAMETER_JOINER.join(typeParameters.stream().map(JavaType::toString).iterator()) + ">";
    }

    public static JavaType of(final TypeReference<?> reference) {
        return of(reference.getType(), Optional.empty());
    }

    public static JavaType of(final Type type) {
        return of(type, Optional.empty());
    }

    public static JavaType of(final Type type, final Optional<TypeResolver> parent) {
        if (type instanceof java.lang.Class<?>) {
            final Class<?> c = (Class<?>) type;

            final List<JavaType> typeParameters;

            if (c.getTypeParameters().length > 0) {
                final TypeResolver p = parent.orElseThrow(
                    () -> new IllegalArgumentException("No type information available"));

                typeParameters = Arrays
                    .stream(c.getTypeParameters())
                    .map(p::resolve)
                    .collect(Collectors.toList());
            } else {
                typeParameters = Collections.emptyList();
            }

            return new JavaType(parent, c, typeParameters);
        }

        if (type instanceof java.lang.reflect.ParameterizedType) {
            final java.lang.reflect.ParameterizedType pt =
                (java.lang.reflect.ParameterizedType) type;
            final TypeResolver p = TypeResolver.of(pt, parent);
            return of(pt.getRawType(), Optional.of(p));
        }

        if (type instanceof TypeVariable<?>) {
            final TypeVariable<?> tv = (TypeVariable<?>) type;
            final TypeResolver p = parent.orElseThrow(
                () -> new IllegalStateException("Unable to resolve variable: " + tv));
            return p.resolve(tv);
        }

        throw new IllegalStateException("Unsupported type: " + type);
    }

    private static List<Parameter> buildParameters(
        final java.lang.reflect.Executable executable, final Optional<TypeResolver> parent
    ) {
        final ImmutableList.Builder<Parameter> parameters = ImmutableList.builder();

        int index = 0;

        for (final java.lang.reflect.Parameter p : executable.getParameters()) {
            final JavaType parameterType =
                of(executable.getGenericParameterTypes()[index++], parent);
            parameters.add(new Parameter(parameterType, ImmutableList.copyOf(p.getAnnotations()),
                p.getName()));
        }

        return parameters.build();
    }

    private static Map<JavaType, JavaType> primitivesToBoxed() {
        final Map<Type, Type> forward = new HashMap<>();

        forward.put(boolean.class, Boolean.class);
        forward.put(byte.class, Byte.class);
        forward.put(char.class, Character.class);
        forward.put(short.class, Short.class);
        forward.put(int.class, Integer.class);
        forward.put(long.class, Long.class);
        forward.put(float.class, Float.class);
        forward.put(double.class, Double.class);

        return Collections.unmodifiableMap(forward
            .entrySet()
            .stream()
            .collect(
                Collectors.toMap(e -> JavaType.of(e.getKey()), e -> JavaType.of(e.getValue()))));
    }

    private static Map<JavaType, JavaType> primitivesToUnboxed() {
        return Collections.unmodifiableMap(primitivesToBoxed()
            .entrySet()
            .stream()
            .collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey)));
    }

    @Data
    public static class Parameter implements AnnotatedType {
        private final JavaType parameterType;
        private final List<Annotation> annotations;
        private final String name;

        @Override
        public Stream<Annotation> getAnnotations() {
            return annotations.stream();
        }
    }

    @Data
    @EqualsAndHashCode(exclude = {"type"})
    public static class Constructor implements ExecutableType, AccessibleType, AnnotatedType {
        private final java.lang.reflect.Constructor<?> type;

        private final List<Parameter> parameters;
        private final List<Annotation> annotations;
        private final int modifiers;

        public Object newInstance(Object... arguments)
            throws IllegalAccessException, InvocationTargetException, InstantiationException {
            return type.newInstance(arguments);
        }

        @Override
        public Stream<Annotation> getAnnotations() {
            return Arrays.stream(type.getAnnotations());
        }
    }

    @Data
    @EqualsAndHashCode(exclude = {"type"})
    public static class Method implements ExecutableType, AccessibleType, AnnotatedType {
        private final java.lang.reflect.Method type;

        private final List<Annotation> annotations;
        private final int modifiers;
        private final JavaType returnType;
        private final String name;
        private final List<Parameter> parameters;

        @Override
        public Stream<Annotation> getAnnotations() {
            return annotations.stream();
        }

        public Object invoke(final Object instance, final Object... arguments)
            throws InvocationTargetException, IllegalAccessException {
            return type.invoke(instance, arguments);
        }
    }

    @Data
    public static class Field implements AccessibleType, AnnotatedType {
        private final List<Annotation> annotations;
        private final int modifiers;
        private final JavaType fieldType;
        private final String name;

        @Override
        public Stream<Annotation> getAnnotations() {
            return annotations.stream();
        }
    }
}
