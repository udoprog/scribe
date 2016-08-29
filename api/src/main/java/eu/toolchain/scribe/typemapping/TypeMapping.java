package eu.toolchain.scribe.typemapping;

import eu.toolchain.scribe.Decoder;
import eu.toolchain.scribe.DecoderFactory;
import eu.toolchain.scribe.Encoder;
import eu.toolchain.scribe.EncoderFactory;
import eu.toolchain.scribe.EntityResolver;
import eu.toolchain.scribe.Flags;
import eu.toolchain.scribe.JavaType;
import eu.toolchain.scribe.StreamEncoder;
import eu.toolchain.scribe.StreamEncoderFactory;

import java.util.Optional;

/**
 * Encapsulates information about a given Type that is used for resolving an encoder.
 *
 * @author udoprog
 */
public interface TypeMapping {
  JavaType getType();

  <Target, Source> Optional<Encoder<Target, Source>> newEncoder(
      EntityResolver resolver, Flags flags, EncoderFactory<Target> factory
  );

  <Target, Source> Optional<StreamEncoder<Target, Source>> newStreamEncoder(
      EntityResolver resolver, Flags flags, StreamEncoderFactory<Target> factory
  );

  <Target, Source> Optional<Decoder<Target, Source>> newDecoder(
      EntityResolver resolver, Flags flags, DecoderFactory<Target> factory
  );

  default <Target, Source> Encoder<Target, Source> newEncoderImmediate(
      EntityResolver resolver, Flags flags, EncoderFactory<Target> factory
  ) {
    return this.<Target, Source>newEncoder(resolver, flags, factory).orElseThrow(
        () -> new IllegalArgumentException("Unable to build encoder for type (" + getType() + ")"));
  }

  default <Target, Source> StreamEncoder<Target, Source> newStreamEncoderImmediate(
      EntityResolver resolver, Flags flags, StreamEncoderFactory<Target> factory
  ) {
    return this.<Target, Source>newStreamEncoder(resolver, flags, factory).orElseThrow(
        () -> new IllegalArgumentException(
            "Unable to build stream encoder for type (" + getType() + ")"));
  }

  default <Target, Source> Decoder<Target, Source> newDecoderImmediate(
      EntityResolver resolver, Flags flags, DecoderFactory<Target> factory
  ) {
    return this.<Target, Source>newDecoder(resolver, flags, factory).orElseThrow(
        () -> new IllegalArgumentException("Unable to build decoder for type (" + getType() + ")"));
  }

  default void initialize(final EntityResolver resolver) {
  }
}
