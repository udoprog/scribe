package eu.toolchain.scribe;

import eu.toolchain.scribe.reflection.JavaType;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class DecoderRegistry<Target> {
  private final List<MatcherPair<DecoderBuilder<Target, ?>>> decoders = new ArrayList<>();

  public <Source> void constant(
      final TypeMatcher matcher, final Decoder<Target, Source> decoder
  ) {
    simple(matcher, () -> decoder);
  }

  @SuppressWarnings("unchecked")
  public <Source> void simple(
      final TypeMatcher matcher, final Supplier<Decoder<Target, Source>> supplier
  ) {
    this.decoders.add(new MatcherPair<>(matcher, (resolver, type, decoder) -> {
      return Stream.of((Decoder<Target, Object>) supplier.get());
    }));
  }

  public <Source> void setup(
      final TypeMatcher matcher, final DecoderBuilder<Target, Source> decoder
  ) {
    this.decoders.add(new MatcherPair<>(matcher, decoder));
  }

  @SuppressWarnings("unchecked")
  public <Source> Stream<Decoder<Target, Source>> newDecoder(
      final EntityResolver resolver, final JavaType type, final DecoderFactory<Target> encoding
  ) {
    return decoders.stream().filter(p -> p.matcher.matches(type)).flatMap(p -> {
      Stream<? extends Decoder<Target, ?>> decoders = p.value.apply(resolver, type, encoding);
      return (Stream<Decoder<Target, Source>>) decoders;
    });
  }

  @Data
  static class MatcherPair<T> {
    private final TypeMatcher matcher;
    private final T value;
  }

  @FunctionalInterface
  public interface DecoderBuilder<T, O> {
    Stream<Decoder<T, O>> apply(
        EntityResolver resolver, JavaType type, DecoderFactory<T> decoder
    );
  }
}
