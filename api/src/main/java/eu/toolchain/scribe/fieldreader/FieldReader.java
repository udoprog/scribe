package eu.toolchain.scribe.fieldreader;

import eu.toolchain.scribe.Annotations;
import eu.toolchain.scribe.JavaType;

/**
 * Read the value of individual fields.
 */
public interface FieldReader {
  Object read(Object instance);

  Annotations annotations();

  JavaType fieldType();
}
