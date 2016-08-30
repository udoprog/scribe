package eu.toolchain.scribe;

import eu.toolchain.scribe.reflection.JavaType;

import java.util.stream.Stream;

public interface EncoderFactory<Target> {
  <Source> Stream<Encoder<Target, Source>> newEncoder(
      EntityResolver resolver, Flags flags, JavaType type
  );

  EntityFieldsEncoder<Target> newEntityEncoder();
}
