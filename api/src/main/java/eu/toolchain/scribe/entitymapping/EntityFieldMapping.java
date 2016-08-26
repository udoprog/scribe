package eu.toolchain.scribe.entitymapping;

import eu.toolchain.scribe.DecoderFactory;
import eu.toolchain.scribe.EncoderFactory;
import eu.toolchain.scribe.EntityResolver;
import eu.toolchain.scribe.StreamEncoderFactory;

import java.util.Optional;

public interface EntityFieldMapping {
  <Target> Optional<? extends EntityFieldEncoder<Target, Object>> newEntityFieldEncoder(
      EntityResolver resolver, EncoderFactory<Target> factory
  );

  <Target> Optional<? extends EntityFieldStreamEncoder<Target, Object>> newEntityFieldStreamEncoder(
      EntityResolver resolver, StreamEncoderFactory<Target> factory
  );

  <Target> Optional<? extends EntityFieldDecoder<Target, Object>> newEntityFieldDecoder(
      EntityResolver resolver, DecoderFactory<Target> factory
  );
}
