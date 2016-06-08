package eu.toolchain.ogt.fieldreader;

import eu.toolchain.ogt.Annotations;
import eu.toolchain.ogt.JavaType;

/**
 * Read the value of individual fields.
 */
public interface FieldReader {
    Object read(Object instance);

    Annotations annotations();

    JavaType fieldType();
}
