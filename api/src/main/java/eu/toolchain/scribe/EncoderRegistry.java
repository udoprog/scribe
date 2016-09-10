package eu.toolchain.scribe;

import eu.toolchain.scribe.reflection.JavaType;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class EncoderRegistry<Target> {
  private final List<EncoderPair<Target, ?>> encoders = new ArrayList<>();

  public <Source> void constant(
      final TypeMatcher matcher, final Encoder<Target, Source> encoder
  ) {
    simple(matcher, () -> encoder);
  }

  @SuppressWarnings("unchecked")
  public <Source> void simple(
      final TypeMatcher matcher, final Supplier<Encoder<Target, Source>> supplier
  ) {
    this.encoders.add(new EncoderPair<>(matcher, (resolver, type, decoder) -> {
      return Stream.of((Encoder<Target, Object>) supplier.get());
    }));
  }

  public <Source> void setup(
      final TypeMatcher matcher, final EncoderBuilder<Target, Source> encoder
  ) {
    this.encoders.add(new EncoderPair<>(matcher, encoder));
  }

  @SuppressWarnings("unchecked")
  public <Source> Stream<Encoder<Target, Source>> newEncoder(
      final EntityResolver resolver, final JavaType type, final EncoderFactory<Target> encoder
  ) {
    return encoders.stream().filter(p -> p.matcher.matches(type)).flatMap(p -> {
      final Stream<? extends Encoder<Target, ?>> apply = p.encoder.apply(resolver, type, encoder);
      return (Stream<Encoder<Target, Source>>) apply;
    });
  }

  @Data
  static class EncoderPair<T, O> {
    private final TypeMatcher matcher;
    private final EncoderBuilder<T, O> encoder;
  }

  @FunctionalInterface
  public interface EncoderBuilder<T, O> {
    Stream<Encoder<T, O>> apply(
        EntityResolver resolver, JavaType type, EncoderFactory<T> encoder
    );
  }
}
