package eu.toolchain.scribe;

import lombok.Data;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * A tree structure of type variables.
 * <p>
 * This data structure allow for type variable lookups in a tree.
 * <p>
 * Typically this would be build from a generic class definition with concrete (parameterized) type
 * information, and be kept inside the {@link JavaType} instance.
 */
@Data
class TypeVariableTree {
  private final Map<TypeVariable<?>, Type> table;
  private final Optional<TypeVariableTree> parent;

  /**
   * Lookup the concrete type for the given variable.
   *
   * @param variable The variable to look up the type for.
   * @return The looked up type.
   */
  public JavaType lookup(final TypeVariable<?> variable) {
    final Type t = table.get(variable);

    if (t != null) {
      return JavaType.of(t, parent);
    }

    final TypeVariableTree p = parent.orElseThrow(
        () -> new IllegalStateException("Unable to lookup variable: " + variable));
    return p.lookup(variable);
  }

  public static TypeVariableTree of(
      final ParameterizedType pt, final Optional<TypeVariableTree> parent
  ) {
    final Class<?> raw = (Class<?>) pt.getRawType();

    final Type[] actual = pt.getActualTypeArguments();
    final TypeVariable<?>[] variables = raw.getTypeParameters();

    if (actual.length != variables.length) {
      throw new IllegalArgumentException("Type argument size mismatch");
    }

    final HashMap<TypeVariable<?>, Type> table = new HashMap<>(actual.length);

    for (int i = 0; i < actual.length; i++) {
      table.put(variables[i], actual[i]);
    }

    return new TypeVariableTree(Collections.unmodifiableMap(table), parent);
  }
}
