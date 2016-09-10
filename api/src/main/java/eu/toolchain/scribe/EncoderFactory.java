package eu.toolchain.scribe;

import eu.toolchain.scribe.reflection.JavaType;

import java.util.stream.Stream;

public interface EncoderFactory<Target> {
  <Source> Stream<Encoder<Target, Source>> newEncoder(
      EntityResolver resolver, JavaType type, Flags flags
  );

  default <Source> Stream<Encoder<Target, Source>> newEncoder(
      EntityResolver resolver, JavaType type
  ) {
    return newEncoder(resolver, type, Flags.empty());
  }

  EntityFieldsEncoder<Target> newEntityEncoder();
}
