package eu.toolchain.scribe;

import eu.toolchain.scribe.reflection.AnnotatedType;
import eu.toolchain.scribe.reflection.JavaType;

import java.util.List;

public interface ExecutableType extends AnnotatedType {
  JavaType getEncapsulatingType();

  List<JavaType.Parameter> getParameters();
}
