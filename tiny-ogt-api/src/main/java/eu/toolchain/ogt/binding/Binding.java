package eu.toolchain.ogt.binding;

import eu.toolchain.ogt.Context;
import eu.toolchain.ogt.EntityDecoder;
import eu.toolchain.ogt.EntityEncoder;

/**
 * Framework for creating and introspecting types using different methods.
 *
 * @see BuilderMethodTypeBinder
 * @author udoprog
 */
public interface Binding {
    Object decodeEntity(EntityDecoder decoder, Context path);

    void encodeEntity(EntityEncoder encoder, Object value, Context path);
}
