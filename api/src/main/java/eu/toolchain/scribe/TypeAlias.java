package eu.toolchain.scribe;

import eu.toolchain.scribe.reflection.JavaType;

public interface TypeAlias {
  JavaType getFromType();

  JavaType getToType();

  Mapping apply(Mapping mapping);
}
