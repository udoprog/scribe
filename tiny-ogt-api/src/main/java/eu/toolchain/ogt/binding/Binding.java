package eu.toolchain.ogt.binding;

import eu.toolchain.ogt.Context;
import eu.toolchain.ogt.EntityDecoder;
import eu.toolchain.ogt.EntityEncoder;
import eu.toolchain.ogt.FieldDecoder;
import eu.toolchain.ogt.FieldEncoder;

/**
 * Framework for creating and introspecting types using different methods.
 *
 * @author udoprog
 */
public interface Binding<T> {
    Object decodeEntity(
        EntityDecoder<T> entityDecoder, FieldDecoder<T> decoder, Context path
    );

    T encodeEntity(
        EntityEncoder<T> entityEncoder, FieldEncoder<T> encoder, Context path, Object value
    );
}
