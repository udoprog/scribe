package eu.toolchain.scribe;

import eu.toolchain.scribe.reflection.JavaType;

import java.util.stream.Stream;

public interface EncodeValue<Source> {
  JavaType getSourceType();

  Mapping<Source> getTargetMapping();

  <Target, EntityTarget> Stream<Encoder<Target, Source>> newEncoder(
      EntityResolver resolver, EncoderFactory<Target, EntityTarget> factory, Flags flags
  );

  <Target> Stream<StreamEncoder<Target, Source>> newStreamEncoder(
      EntityResolver resolver, StreamEncoderFactory<Target> factory, Flags flags
  );

  default void initialize(final EntityResolver resolver) {
  }
}
