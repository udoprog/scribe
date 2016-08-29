package eu.toolchain.scribe;

import java.util.stream.Stream;

public interface StreamEncoderFactory<Target> {
  <Source> Stream<StreamEncoder<Target, Source>> newStreamEncoder(
      EntityResolver resolver, Flags flags, JavaType type
  );

  EntityFieldsStreamEncoder<Target> newEntityStreamEncoder();
}
