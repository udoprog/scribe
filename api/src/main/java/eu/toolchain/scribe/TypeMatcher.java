package eu.toolchain.scribe;

import lombok.Data;

import java.lang.reflect.Type;
import java.util.Arrays;

/**
 * Utility functions for matching types.
 */
public interface TypeMatcher {
  /**
   * Check weither the current matcher matches the given type.
   *
   * @param type Type to match against.
   * @return {@code true} if the type matches the current matcher.
   */
  boolean matches(final JavaType type);

  /**
   * Creates a matcher for a given primitive type.
   *
   * @param primitiveType The primitive type to match.
   * @return A new matcher for the given primitive type.
   */
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

  static TypeMatcher not(TypeMatcher matcher) {
    return type -> !matcher.matches(type);
  }

  static TypeMatcher isArray() {
    return JavaType::isArray;
  }

  static TypeMatcher inPackage(final String packageName) {
    return new InPackage(packageName);
  }

  static TypeMatcher any() {
    return new Any();
  }

  static TypeMatcher anyOf(final TypeMatcher... matchers) {
    return type -> Arrays.stream(matchers).anyMatch(m -> m.matches(type));
  }

  static TypeMatcher allOf(final TypeMatcher... matchers) {
    return type -> Arrays.stream(matchers).allMatch(m -> m.matches(type));
  }

  static TypeMatcher instance(final Class<?> base) {
    return type -> base.isAssignableFrom(type.getType());
  }

  static TypeMatcher type(final Class<?> base, final TypeMatcher... parameters) {
    if (base.getTypeParameters().length != parameters.length) {
      throw new IllegalArgumentException("Number of type arguments for class " + base + " (" +
          base.getTypeParameters().length + ") is not the expected (" +
          parameters.length + ")");
    }

    return new Parameterized(base, parameters);
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
