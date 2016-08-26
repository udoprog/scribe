package eu.toolchain.scribe;

import java.lang.reflect.Type;

public interface TypeDecoderProvider<Target> {
  Decoder<Target, Object> newDecoder(final Type type);

  <Source> Decoder<Target, Source> newDecoder(final Class<Source> type);

  <Source> Decoder<Target, Source> newDecoder(final TypeReference<Source> type);
}
