package eu.toolchain.scribe;

import eu.toolchain.scribe.reflection.JavaType;

import java.util.Optional;

public interface EncodeValue {
  JavaType getSourceType();

  Mapping getTargetMapping();

  <Target, Source> Optional<Encoder<Target, Source>> newEncoder(
      EntityResolver resolver, EncoderFactory<Target> factory
  );

  <Target, Source> Optional<StreamEncoder<Target, Source>> newStreamEncoder(
      EntityResolver resolver, StreamEncoderFactory<Target> factory
  );

  default void initialize(final EntityResolver resolver) {
  }
}
