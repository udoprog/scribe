package eu.toolchain.ogt.type;

import java.util.Optional;

import eu.toolchain.ogt.Context;
import eu.toolchain.ogt.EntityResolver;
import eu.toolchain.ogt.FieldDecoder;
import eu.toolchain.ogt.FieldEncoder;
import eu.toolchain.ogt.JavaType;

/**
 * Type mappings are converters for various types.
 *
 * They can represent everything from primitive types, to full-blown entities.
 *
 * @see PrimitiveTypeMapping
 * @see ConcreteEntityTypeMapping
 * @author udoprog
 */
public interface TypeMapping {
    JavaType getType();

    <T> Object decode(FieldDecoder<T> decoder, Context path, T value);

    <T> T encode(FieldEncoder<T> encoder, Context path, Object value);

    default Optional<Object> asOptional(Object value) {
        return Optional.of(value);
    }

    default Optional<?> fromOptional(Optional<?> value) {
        return value;
    }

    /**
     * Perform initialization of all required dependencies recursively.
     *
     * This is a two-step process as to support circular dependencies.
     */
    default void initialize(final EntityResolver resolver) {
    }
}
