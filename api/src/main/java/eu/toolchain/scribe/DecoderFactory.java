package eu.toolchain.scribe;

import eu.toolchain.scribe.reflection.JavaType;

import java.util.stream.Stream;

public interface DecoderFactory<Target> {
  <Source> Stream<Decoder<Target, Source>> newDecoder(
      EntityResolver resolver, Flags flags, JavaType type
  );

  Decoded<EntityFieldsDecoder<Target>> newEntityDecoder(Target instance);
}
