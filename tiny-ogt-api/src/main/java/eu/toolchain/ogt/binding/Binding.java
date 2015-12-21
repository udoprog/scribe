package eu.toolchain.ogt.binding;

import eu.toolchain.ogt.Context;
import eu.toolchain.ogt.EntityDecoder;
import eu.toolchain.ogt.EntityEncoder;
import eu.toolchain.ogt.FieldDecoder;
import eu.toolchain.ogt.FieldEncoder;

/**
 * Framework for creating and introspecting types using different methods.
 *
 * @see BuilderMethodTypeBinder
 * @author udoprog
 */
public interface Binding {
    Object decodeEntity(EntityDecoder entityDecoder, FieldDecoder decoder, Context path);

    Object encodeEntity(EntityEncoder entityEncoder, FieldEncoder encoder, Object value,
            Context path);
}
