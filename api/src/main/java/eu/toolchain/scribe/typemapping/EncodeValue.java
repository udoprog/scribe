package eu.toolchain.scribe.typemapping;

import eu.toolchain.scribe.Encoder;
import eu.toolchain.scribe.EncoderFactory;
import eu.toolchain.scribe.EntityResolver;
import eu.toolchain.scribe.JavaType;
import eu.toolchain.scribe.StreamEncoder;
import eu.toolchain.scribe.StreamEncoderFactory;

import java.util.Optional;

public interface EncodeValue {
  JavaType getEntityType();

  TypeMapping getTargetMapping();

  <Target, Source> Optional<Encoder<Target, Source>> newEncoder(
      EntityResolver resolver, EncoderFactory<Target> factory
  );

  <Target, Source> Optional<StreamEncoder<Target, Source>> newStreamEncoder(
      EntityResolver resolver, StreamEncoderFactory<Target> factory
  );

  default void initialize(final EntityResolver resolver) {
  }
}
