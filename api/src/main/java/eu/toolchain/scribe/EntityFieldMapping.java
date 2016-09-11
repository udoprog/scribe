package eu.toolchain.scribe;

import java.util.stream.Stream;

public interface EntityFieldMapping<Source> {
  <Target, EntityTarget> Stream<? extends EntityFieldEncoder<Target, Source>> newEntityFieldEncoder(
      EntityResolver resolver, EncoderFactory<Target, EntityTarget> factory
  );

  <Target> Stream<? extends EntityFieldStreamEncoder<Target, Source>> newEntityFieldStreamEncoder(
      EntityResolver resolver, StreamEncoderFactory<Target> factory
  );

  <Target, EntityTarget> Stream<? extends EntityFieldDecoder<Target, Source>> newEntityFieldDecoder(
      EntityResolver resolver, DecoderFactory<Target, EntityTarget> factory
  );
}
