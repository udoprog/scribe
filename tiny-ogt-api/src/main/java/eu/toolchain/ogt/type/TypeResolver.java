package eu.toolchain.ogt.type;

import com.google.common.collect.ImmutableMap;
import lombok.Data;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Map;
import java.util.Optional;

@Data
class TypeResolver {
    private final Map<TypeVariable<?>, Type> table;
    private final Optional<TypeResolver> parent;

    public JavaType resolve(final TypeVariable<?> variable) {
        final Type t = table.get(variable);

        if (t != null) {
            return JavaType.of(t, parent);
        }

        final TypeResolver p = parent.orElseThrow(
            () -> new IllegalStateException("Unable to resolve variable: " + variable));
        return p.resolve(variable);
    }

    public static TypeResolver of(final ParameterizedType pt, final Optional<TypeResolver> parent) {
        final Class<?> raw = (Class<?>) pt.getRawType();

        final Type[] actual = pt.getActualTypeArguments();
        final TypeVariable<?>[] variables = raw.getTypeParameters();

        if (actual.length != variables.length) {
            throw new IllegalArgumentException("Type argument size mismatch");
        }

        final ImmutableMap.Builder<TypeVariable<?>, Type> table = ImmutableMap.builder();

        for (int i = 0; i < actual.length; i++) {
            table.put(variables[i], actual[i]);
        }

        return new TypeResolver(table.build(), parent);
    }
}
