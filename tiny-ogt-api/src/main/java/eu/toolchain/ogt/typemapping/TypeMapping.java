package eu.toolchain.ogt.typemapping;

import eu.toolchain.ogt.Decoder;
import eu.toolchain.ogt.Encoder;
import eu.toolchain.ogt.EncodingFactory;
import eu.toolchain.ogt.EntityResolver;

import java.lang.reflect.Type;
import java.util.Optional;

/**
 * Encapsulates information about a given Type that is used for resolving an encoder.
 *
 * @author udoprog
 */
public interface TypeMapping {
    Type getType();

    <Target, Source> Optional<Encoder<Target, Source>> newEncoder(
        EntityResolver resolver, EncodingFactory<Target> factory
    );

    <Target, Source> Optional<Decoder<Target, Source>> newDecoder(
        EntityResolver resolver, EncodingFactory<Target> factory
    );

    default <Target, Source> Encoder<Target, Source> newEncoderImmediate(
        EntityResolver resolver, EncodingFactory<Target> factory
    ) {
        return this.<Target, Source>newEncoder(resolver, factory).orElseThrow(
            () -> new IllegalArgumentException(
                "Unable to build encoder for type (" + getType() + ")"));
    }

    default <Target, Source> Decoder<Target, Source> newDecoderImmediate(
        EntityResolver resolver, EncodingFactory<Target> factory
    ) {
        return this.<Target, Source>newDecoder(resolver, factory).orElseThrow(
            () -> new IllegalArgumentException(
                "Unable to build decoder for type (" + getType() + ")"));
    }

    default void initialize(final EntityResolver resolver) {
    }
}
