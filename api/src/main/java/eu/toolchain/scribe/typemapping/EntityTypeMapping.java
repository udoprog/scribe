package eu.toolchain.scribe.typemapping;

import eu.toolchain.scribe.Decoder;
import eu.toolchain.scribe.DecoderFactory;
import eu.toolchain.scribe.Encoder;
import eu.toolchain.scribe.EncoderFactory;
import eu.toolchain.scribe.EntityDecoder;
import eu.toolchain.scribe.EntityEncoder;
import eu.toolchain.scribe.EntityResolver;
import eu.toolchain.scribe.EntityStreamEncoder;
import eu.toolchain.scribe.Flags;
import eu.toolchain.scribe.StreamEncoder;
import eu.toolchain.scribe.StreamEncoderFactory;

import java.util.Optional;

public interface EntityTypeMapping extends TypeMapping {
  default Optional<String> typeName() {
    return Optional.empty();
  }

  @SuppressWarnings("unchecked")
  @Override
  default <Target, Source> Optional<Encoder<Target, Source>> newEncoder(
      final EntityResolver resolver, final Flags flags, final EncoderFactory<Target> factory
  ) {
    return Optional.of((Encoder<Target, Source>) newEntityTypeEncoder(resolver, factory));
  }

  @SuppressWarnings("unchecked")
  @Override
  default <Target, Source> Optional<StreamEncoder<Target, Source>> newStreamEncoder(
      final EntityResolver resolver, final Flags flags, final StreamEncoderFactory<Target> factory
  ) {
    return Optional.of(
        (StreamEncoder<Target, Source>) newEntityTypeStreamEncoder(resolver, factory));
  }

  @SuppressWarnings("unchecked")
  @Override
  default <Target, Source> Optional<Decoder<Target, Source>> newDecoder(
      EntityResolver resolver, final Flags flags, DecoderFactory<Target> factory
  ) {
    return Optional.of((Decoder<Target, Source>) newEntityTypeDecoder(resolver, factory));
  }

  <Target> EntityEncoder<Target, Object> newEntityTypeEncoder(
      EntityResolver resolver, EncoderFactory<Target> factory
  );

  <Target> EntityStreamEncoder<Target, Object> newEntityTypeStreamEncoder(
      EntityResolver resolver, StreamEncoderFactory<Target> factory
  );

  <Target> EntityDecoder<Target, Object> newEntityTypeDecoder(
      EntityResolver resolver, DecoderFactory<Target> factory
  );
}
