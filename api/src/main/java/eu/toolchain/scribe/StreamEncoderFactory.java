package eu.toolchain.scribe;

import java.util.stream.Stream;

public interface StreamEncoderFactory<Target> {
  <Source> Stream<StreamEncoder<Target, Source>> newStreamEncoder(
      EntityResolver resolver, JavaType type
  );

  EntityFieldsStreamEncoder<Target> newEntityStreamEncoder();
}
