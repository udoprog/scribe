package eu.toolchain.scribe;

import java.util.stream.Stream;

public interface DecoderFactory<Target> {
  <Source> Stream<Decoder<Target, Source>> newDecoder(EntityResolver resolver, JavaType type);

  Decoded<EntityFieldsDecoder<Target>> newEntityDecoder(Target instance);
}
