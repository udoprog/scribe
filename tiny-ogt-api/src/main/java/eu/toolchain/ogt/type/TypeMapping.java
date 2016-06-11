package eu.toolchain.ogt.type;

import eu.toolchain.ogt.Context;
import eu.toolchain.ogt.EntityResolver;
import eu.toolchain.ogt.TypeDecoder;
import eu.toolchain.ogt.TypeEncoder;
import eu.toolchain.ogt.JavaType;

import java.util.Optional;

/**
 * Type mappings are converters for various types.
 * <p>
 * They can represent everything from primitive types, to full-blown entities.
 *
 * @author udoprog
 */
public interface TypeMapping {
    JavaType getType();

    <T> Object decode(TypeDecoder<T> decoder, Context path, T value);

    <T> T encode(TypeEncoder<T> encoder, Context path, Object value);

    default Optional<Object> asOptional(Object value) {
        return Optional.of(value);
    }

    default Optional<?> fromOptional(Optional<?> value) {
        return value;
    }

    /**
     * Perform initialization of all required dependencies recursively.
     * <p>
     * This is a two-step process as to support circular dependencies.
     */
    default void initialize(final EntityResolver resolver) {
    }
}
