package eu.toolchain.scribe;

import java.util.stream.Stream;

public interface EntityFieldMapping {
  <Target, EntityTarget> Stream<? extends EntityFieldEncoder<Target, Object>> newEntityFieldEncoder(
      EntityResolver resolver, EncoderFactory<Target, EntityTarget> factory
  );

  <Target> Stream<? extends EntityFieldStreamEncoder<Target, Object>> newEntityFieldStreamEncoder(
      EntityResolver resolver, StreamEncoderFactory<Target> factory
  );

  <Target, EntityTarget> Stream<? extends EntityFieldDecoder<Target, Object>> newEntityFieldDecoder(
      EntityResolver resolver, DecoderFactory<Target, EntityTarget> factory
  );
}
