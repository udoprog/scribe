package eu.toolchain.scribe;

import java.util.stream.Stream;

public interface EncoderFactory<Target> {
  <Source> Stream<Encoder<Target, Source>> newEncoder(EntityResolver resolver, JavaType type);

  EntityFieldsEncoder<Target> newEntityEncoder();
}