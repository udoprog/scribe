package eu.toolchain.scribe.reflection;

import java.lang.reflect.Modifier;

/**
 * Describes a type that is accessible through a set of modifiers.
 */
public interface AccessibleType {
  /**
   * If the type is public.
   *
   * @return {@code true} if the type is public.
   */
  default boolean isPublic() {
    return (getModifiers() & Modifier.PUBLIC) > 0;
  }

  /**
   * If the type is static.
   *
   * @return {@code true} if the type is static.
   */
  default boolean isStatic() {
    return (getModifiers() & Modifier.STATIC) > 0;
  }

  /**
   * If the type is abstract.
   *
   * @return {@code true} if the type is abstract.
   */
  default boolean isAbstract() {
    return (getModifiers() & Modifier.ABSTRACT) > 0;
  }

  /**
   * Get the integer set of modifiers, as specified in {@link java.lang.reflect.Modifier}.
   *
   * @return The set of modifiers for this type.
   */
  int getModifiers();
}
