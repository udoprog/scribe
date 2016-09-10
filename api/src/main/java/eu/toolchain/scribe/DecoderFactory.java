package eu.toolchain.scribe;

import eu.toolchain.scribe.reflection.JavaType;

import java.util.stream.Stream;

public interface DecoderFactory<Target> {
  <Source> Stream<Decoder<Target, Source>> newDecoder(
      EntityResolver resolver, JavaType type, Flags flags
  );

  default <Source> Stream<Decoder<Target, Source>> newDecoder(
      EntityResolver resolver, JavaType type
  ) {
    return newDecoder(resolver, type, Flags.empty());
  }

  Decoded<EntityFieldsDecoder<Target>> newEntityDecoder(Target instance);
}
