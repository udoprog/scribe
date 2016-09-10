package eu.toolchain.scribe;

import java.util.stream.Stream;

public interface EntityFieldMapping {
  <Target> Stream<? extends EntityFieldEncoder<Target, Object>> newEntityFieldEncoder(
      EntityResolver resolver, EncoderFactory<Target> factory
  );

  <Target> Stream<? extends EntityFieldStreamEncoder<Target, Object>> newEntityFieldStreamEncoder(
      EntityResolver resolver, StreamEncoderFactory<Target> factory
  );

  <Target> Stream<? extends EntityFieldDecoder<Target, Object>> newEntityFieldDecoder(
      EntityResolver resolver, DecoderFactory<Target> factory
  );
}
