package eu.toolchain.scribe;

import eu.toolchain.scribe.reflection.JavaType;

import java.util.stream.Stream;

public interface DecodeValue {
  JavaType getSourceType();

  <Target, EntityTarget, Source> Stream<Decoder<Target, Source>> newDecoder(
      EntityResolver resolver, DecoderFactory<Target, EntityTarget> factory, Flags flags
  );

  default void initialize(final EntityResolver resolver) {
  }
}
