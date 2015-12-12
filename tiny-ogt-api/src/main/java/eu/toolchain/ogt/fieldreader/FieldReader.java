package eu.toolchain.ogt.fieldreader;

/**
 * Read the value of individual fields.
 */
public interface FieldReader {
    Object read(Object instance);
}
