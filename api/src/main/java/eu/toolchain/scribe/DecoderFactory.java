package eu.toolchain.scribe;

import eu.toolchain.scribe.reflection.JavaType;

import java.util.stream.Stream;

public interface DecoderFactory<Target, EntityTarget> {
  <Source> Stream<Decoder<Target, Source>> newDecoder(
      EntityResolver resolver, JavaType type, Flags flags
  );

  default <Source> Stream<Decoder<Target, Source>> newDecoder(
      EntityResolver resolver, JavaType type
  ) {
    return newDecoder(resolver, type, Flags.empty());
  }

  EntityFieldsDecoder<Target> newEntityDecoder(EntityTarget instance);

  Decoded<EntityTarget> valueAsEntity(Target instance);
}
