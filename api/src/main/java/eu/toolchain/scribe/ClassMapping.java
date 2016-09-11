package eu.toolchain.scribe;

import java.util.Optional;
import java.util.stream.Stream;

public interface ClassMapping<Source> extends Mapping<Source> {
  Optional<String> typeName();

  <Target, EntityTarget> EntityEncoder<Target, EntityTarget, Source> newEntityTypeEncoder(
      EntityResolver resolver, EncoderFactory<Target, EntityTarget> factory
  );

  <Target> EntityStreamEncoder<Target, Source> newEntityTypeStreamEncoder(
      EntityResolver resolver, StreamEncoderFactory<Target> factory
  );

  <Target, EntityTarget> EntityDecoder<Target, EntityTarget, Source> newEntityTypeDecoder(
      EntityResolver resolver, DecoderFactory<Target, EntityTarget> factory
  );

  @Override
  default <Target, EntityTarget> Stream<Encoder<Target, Source>> newEncoder(
      final EntityResolver resolver, final EncoderFactory<Target, EntityTarget> factory,
      final Flags flags
  ) {
    return Stream.of(newEntityTypeEncoder(resolver, factory));
  }

  @Override
  default <Target> Stream<StreamEncoder<Target, Source>> newStreamEncoder(
      final EntityResolver resolver, final StreamEncoderFactory<Target> factory, final Flags flags
  ) {
    return Stream.of(newEntityTypeStreamEncoder(resolver, factory));
  }

  @Override
  default <Target, EntityTarget> Stream<Decoder<Target, Source>> newDecoder(
      EntityResolver resolver, DecoderFactory<Target, EntityTarget> factory, final Flags flags
  ) {
    return Stream.of(newEntityTypeDecoder(resolver, factory));
  }
}
