package eu.toolchain.scribe;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class StreamEncodingRegistry<Target> {
  private final List<StreamEncoderPair<Target, ?>> streamEncoders = new ArrayList<>();

  public <Source> void streamEncoder(
      final TypeMatcher matcher, final StreamEncoderBuilder<Target, Source> encoder
  ) {
    this.streamEncoders.add(new StreamEncoderPair<>(matcher, encoder));
  }

  @SuppressWarnings("unchecked")
  public <Source> Stream<StreamEncoder<Target, Source>> newStreamEncoder(
      final EntityResolver resolver, final JavaType type,
      final StreamEncoderFactory<Target> encoding
  ) {
    return streamEncoders
        .stream()
        .filter(p -> p.matcher.matches(type))
        .map(p -> (StreamEncoder<Target, Source>) p.encoder.apply(resolver, type, encoding));
  }

  @Data
  static class StreamEncoderPair<T, O> {
    private final TypeMatcher matcher;
    private final StreamEncoderBuilder<T, O> encoder;
  }

  @FunctionalInterface
  public interface StreamEncoderBuilder<T, O> {
    StreamEncoder<T, O> apply(
        EntityResolver resolver, JavaType type, StreamEncoderFactory<T> encoding
    );
  }
}
