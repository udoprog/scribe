package eu.toolchain.scribe;

import eu.toolchain.scribe.reflection.JavaType;

import java.util.List;

public interface ExecutableType {
  List<JavaType.Parameter> getParameters();
}
