package eu.toolchain.scribe;

import eu.toolchain.scribe.reflection.Annotations;
import eu.toolchain.scribe.reflection.JavaType;

/**
 * Read the value of individual fields.
 */
public interface FieldReader {
  Object read(Context path, Object instance);

  Annotations annotations();

  JavaType fieldType();
}
