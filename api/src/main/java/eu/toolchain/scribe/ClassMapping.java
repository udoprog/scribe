package eu.toolchain.scribe;

import java.util.Optional;

public interface ClassMapping extends Mapping {
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
