package eu.toolchain.scribe;

import eu.toolchain.scribe.reflection.JavaType;

import java.util.stream.Stream;

public interface StreamEncoderFactory<Target> {
  <Source> Stream<StreamEncoder<Target, Source>> newStreamEncoder(
      EntityResolver resolver, JavaType type, Flags flags
  );

  default <Source> Stream<StreamEncoder<Target, Source>> newStreamEncoder(
      EntityResolver resolver, JavaType type
  ) {
    return newStreamEncoder(resolver, type, Flags.empty());
  }

  EntityFieldsStreamEncoder<Target> newEntityStreamEncoder();
}
