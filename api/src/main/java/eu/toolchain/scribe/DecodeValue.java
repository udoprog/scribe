package eu.toolchain.scribe;

import eu.toolchain.scribe.reflection.JavaType;

import java.util.Optional;

public interface DecodeValue {
  JavaType getSourceType();

  <Target, Source> Optional<Decoder<Target, Source>> newDecoder(
      EntityResolver resolver, DecoderFactory<Target> factory
  );

  default void initialize(final EntityResolver resolver) {
  }
}
