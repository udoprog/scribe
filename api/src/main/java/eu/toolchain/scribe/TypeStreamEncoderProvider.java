package eu.toolchain.scribe;

import java.lang.reflect.Type;

public interface TypeStreamEncoderProvider<Target> {
  StreamEncoder<Target, Object> newStreamEncoder(final Type type);

  <Source> StreamEncoder<Target, Source> newStreamEncoder(final Class<Source> type);

  <Source> StreamEncoder<Target, Source> newStreamEncoder(final TypeReference<Source> type);
}
