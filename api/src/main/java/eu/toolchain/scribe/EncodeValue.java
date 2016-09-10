package eu.toolchain.scribe;

import eu.toolchain.scribe.reflection.JavaType;

import java.util.stream.Stream;

public interface EncodeValue {
  JavaType getSourceType();

  Mapping getTargetMapping();

  <Target, EntityTarget, Source> Stream<Encoder<Target, Source>> newEncoder(
      EntityResolver resolver, EncoderFactory<Target, EntityTarget> factory, Flags flags
  );

  <Target, Source> Stream<StreamEncoder<Target, Source>> newStreamEncoder(
      EntityResolver resolver, StreamEncoderFactory<Target> factory, Flags flags
  );

  default void initialize(final EntityResolver resolver) {
  }
}
