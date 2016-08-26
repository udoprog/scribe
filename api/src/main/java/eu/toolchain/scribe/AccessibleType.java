package eu.toolchain.scribe;

import java.lang.reflect.Modifier;

public interface AccessibleType {
  default boolean isPublic() {
    return (getModifiers() & Modifier.PUBLIC) > 0;
  }

  default boolean isStatic() {
    return (getModifiers() & Modifier.STATIC) > 0;
  }

  default boolean isAbstract() {
    return (getModifiers() & Modifier.ABSTRACT) > 0;
  }

  int getModifiers();
}
