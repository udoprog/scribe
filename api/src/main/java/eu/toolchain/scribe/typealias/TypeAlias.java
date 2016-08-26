package eu.toolchain.scribe.typealias;

import eu.toolchain.scribe.JavaType;
import eu.toolchain.scribe.typemapping.TypeMapping;

public interface TypeAlias {
  JavaType getFromType();

  JavaType getToType();

  TypeMapping apply(TypeMapping mapping);
}
