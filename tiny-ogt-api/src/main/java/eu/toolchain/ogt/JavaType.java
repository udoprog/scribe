package eu.toolchain.ogt;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@EqualsAndHashCode
public class JavaType {
    public static final Joiner PARAMETER_JOINER = Joiner.on(", ");

    public static final Map<Class<?>, Class<?>> PRIMITIVE_BUILTINS = new HashMap<>();

    static {
        PRIMITIVE_BUILTINS.put(boolean.class, Boolean.class);
        PRIMITIVE_BUILTINS.put(byte.class, Byte.class);
        PRIMITIVE_BUILTINS.put(char.class, Character.class);
        PRIMITIVE_BUILTINS.put(short.class, Short.class);
        PRIMITIVE_BUILTINS.put(int.class, Integer.class);
        PRIMITIVE_BUILTINS.put(long.class, Long.class);
        PRIMITIVE_BUILTINS.put(float.class, Float.class);
        PRIMITIVE_BUILTINS.put(double.class, Double.class);
        PRIMITIVE_BUILTINS.put(void.class, Void.class);
    }

    public static final Set<Class<?>> PRIMITIVES = ImmutableSet.<Class<?>> builder()
            .addAll(PRIMITIVE_BUILTINS.keySet()).addAll(PRIMITIVE_BUILTINS.values()).build();

    private final Class<?> raw;
    private final List<JavaType> parameters;

    public JavaType(Class<?> raw) {
        this(raw, ImmutableList.of());
    }

    public int getParameterCount() {
        return parameters.size();
    }

    public JavaType getContainedType(int index) {
        return Objects.requireNonNull(parameters.get(index));
    }

    public JavaType boxed() {
        final Class<?> box = box(raw);
        return new JavaType(box, parameters);
    }

    public boolean isPrimitive() {
        return PRIMITIVES.contains(raw);
    }

    public boolean isVoid() {
        return raw == void.class || raw == Void.TYPE;
    }

    public Class<?> getRawClass() {
        if (raw instanceof Class) {
            return (Class<?>) raw;
        }

        throw new IllegalStateException("Type is not a class");
    }

    public boolean isParameterized() {
        return !parameters.isEmpty();
    }

    public static JavaType construct(final Type type) {
        if (type instanceof GenericArrayType) {
            final GenericArrayType g = (GenericArrayType) type;
            final JavaType component;

            try {
                component = construct(g.getGenericComponentType());
            } catch (final Exception e) {
                throw new IllegalArgumentException("Failed to construct generic type: " + type, e);
            }

            final Class<?> arrayType = Array.newInstance(component.getRawClass(), 0).getClass();
            return construct(arrayType);
        }

        if (type instanceof WildcardType) {
            final WildcardType w = (WildcardType) type;

            try {
                return construct(w.getUpperBounds()[0]);
            } catch (final Exception e) {
                throw new IllegalArgumentException("Failed to construct wildcard type: " + type, e);
            }
        }

        if (type instanceof TypeVariable) {
            final TypeVariable<?> v = (TypeVariable<?>) type;

            if (v.getBounds().length > 1) {
                throw new IllegalArgumentException(
                        "TypeVariable with more than one bound is not supported: " + type);
            }

            try {
                return construct(v.getBounds()[0]);
            } catch (final Exception e) {
                throw new IllegalArgumentException("Failed to construct type variable: " + type, e);
            }
        }

        if (type instanceof ParameterizedType) {
            final ParameterizedType p = (ParameterizedType) type;
            final List<JavaType> parameters;

            try {
                parameters = ImmutableList.copyOf(Arrays.stream(p.getActualTypeArguments())
                        .map(JavaType::construct).iterator());
            } catch (final Exception e) {
                throw new IllegalArgumentException(
                        "Failed to construct parameters from type: " + type, e);
            }

            final Type raw = p.getRawType();

            if (!(raw instanceof Class)) {
                throw new IllegalArgumentException(
                        "Raw type for parameterized not supported: " + type);
            }

            return new JavaType((Class<?>) raw, parameters);
        }

        if (!(type instanceof Class)) {
            throw new IllegalArgumentException("Type not supported: " + type);
        }

        return new JavaType((Class<?>) type);
    }

    public static JavaType of(Class<?> raw, JavaType... parameters) {
        return new JavaType(raw, ImmutableList.copyOf(parameters));
    }

    @Override
    public String toString() {
        if (parameters.isEmpty()) {
            return raw.getCanonicalName();
        }

        return raw.getCanonicalName() + "<" + PARAMETER_JOINER.join(parameters) + ">";
    }

    static Class<?> box(final Class<?> input) {
        return PRIMITIVE_BUILTINS.getOrDefault(input, input);
    }
}
