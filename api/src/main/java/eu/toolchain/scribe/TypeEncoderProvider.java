package eu.toolchain.scribe;

import java.lang.reflect.Type;

public interface TypeEncoderProvider<Target> {
  Encoder<Target, Object> newEncoderForType(final Type type);

  <Source> Encoder<Target, Source> newEncoder(final Class<Source> type);

  <Source> Encoder<Target, Source> newEncoder(final TypeReference<Source> type);
}
