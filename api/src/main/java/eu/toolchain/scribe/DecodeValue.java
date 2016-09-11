package eu.toolchain.scribe;

import eu.toolchain.scribe.reflection.JavaType;

import java.util.stream.Stream;

public interface DecodeValue<Source> {
  JavaType getSourceType();

  <Target, EntityTarget> Stream<Decoder<Target, Source>> newDecoder(
      EntityResolver resolver, DecoderFactory<Target, EntityTarget> factory, Flags flags
  );

  default void initialize(final EntityResolver resolver) {
  }
}
