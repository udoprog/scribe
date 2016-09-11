package eu.toolchain.scribe;

import eu.toolchain.scribe.reflection.JavaType;

public interface TypeAlias<From, To> {
  JavaType getFromType();

  JavaType getToType();

  Mapping<To> apply(Mapping<From> mapping);
}
