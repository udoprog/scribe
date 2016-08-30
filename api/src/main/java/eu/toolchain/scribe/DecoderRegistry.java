package eu.toolchain.scribe;

import eu.toolchain.scribe.reflection.JavaType;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class DecoderRegistry<Target> {
  private final List<MatcherPair<DecoderBuilder<Target, ?>>> decoders = new ArrayList<>();

  public <Source> void decoder(
      final TypeMatcher matcher, final DecoderBuilder<Target, Source> decoder
  ) {
    this.decoders.add(new MatcherPair<>(matcher, decoder));
  }

  @SuppressWarnings("unchecked")
  public <Source> Stream<Decoder<Target, Source>> newDecoder(
      final EntityResolver resolver, final JavaType type, final DecoderFactory<Target> encoding
  ) {
    return decoders
        .stream()
        .filter(p -> p.matcher.matches(type))
        .map(p -> (Decoder<Target, Source>) p.value.apply(resolver, type, encoding));
  }

  @Data
  static class MatcherPair<T> {
    private final TypeMatcher matcher;
    private final T value;
  }

  @FunctionalInterface
  public interface DecoderBuilder<T, O> {
    Decoder<T, O> apply(
        EntityResolver resolver, JavaType type, DecoderFactory<T> decoder
    );
  }

  @FunctionalInterface
  public interface DecoderFilter<Target, Source> {
    Decoder<Target, Source> apply(Decoder<Target, Source> parent);
  }
}
