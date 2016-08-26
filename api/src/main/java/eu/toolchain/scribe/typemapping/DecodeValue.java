package eu.toolchain.scribe.typemapping;

import eu.toolchain.scribe.Decoder;
import eu.toolchain.scribe.DecoderFactory;
import eu.toolchain.scribe.EntityResolver;
import eu.toolchain.scribe.JavaType;

import java.util.Optional;

public interface DecodeValue {
  JavaType getEntityType();

  <Target, Source> Optional<Decoder<Target, Source>> newDecoder(
      EntityResolver resolver, DecoderFactory<Target> factory
  );

  default void initialize(final EntityResolver resolver) {
  }
}
