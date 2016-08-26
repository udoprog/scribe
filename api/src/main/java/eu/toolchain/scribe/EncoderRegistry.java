package eu.toolchain.scribe;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class EncoderRegistry<Target> {
  private final List<EncoderPair<Target, ?>> encoders = new ArrayList<>();

  public <Source> void encoder(
      final TypeMatcher matcher, final EncoderBuilder<Target, Source> encoder
  ) {
    this.encoders.add(new EncoderPair<>(matcher, encoder));
  }

  @SuppressWarnings("unchecked")
  public <Source> Stream<Encoder<Target, Source>> newEncoder(
      final EntityResolver resolver, final JavaType type, final EncoderFactory<Target> encoder
  ) {
    return encoders
        .stream()
        .filter(p -> p.matcher.matches(type))
        .map(p -> (Encoder<Target, Source>) p.encoder.apply(resolver, type, encoder));
  }

  @Data
  static class EncoderPair<T, O> {
    private final TypeMatcher matcher;
    private final EncoderBuilder<T, O> encoder;
  }

  @FunctionalInterface
  public interface EncoderBuilder<T, O> {
    Encoder<T, O> apply(
        EntityResolver resolver, JavaType type, EncoderFactory<T> encoder
    );
  }
}
