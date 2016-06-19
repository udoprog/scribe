package eu.toolchain.ogt.fieldreader;

import eu.toolchain.ogt.Annotations;

import java.lang.reflect.Type;

/**
 * Read the value of individual fields.
 */
public interface FieldReader {
    Object read(Object instance);

    Annotations annotations();

    Type fieldType();
}
