package eu.toolchain.scribe;

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
