package eu.toolchain.scribe;

import eu.toolchain.scribe.reflection.JavaType;

import java.util.stream.Stream;

public interface StreamEncoderFactory<Target> {
  <Source> Stream<StreamEncoder<Target, Source>> newStreamEncoder(
      EntityResolver resolver, Flags flags, JavaType type
  );

  EntityFieldsStreamEncoder<Target> newEntityStreamEncoder();
}
