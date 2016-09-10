package eu.toolchain.scribe;

import eu.toolchain.scribe.reflection.JavaType;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class StreamEncodingRegistry<Target> {
  private final List<StreamEncoderPair<Target, ?>> streamEncoders = new ArrayList<>();

  @SuppressWarnings("unchecked")
  public <Source> void constant(
      final TypeMatcher matcher, final StreamEncoder<Target, Source> streamEncoder
  ) {
    simple(matcher, () -> streamEncoder);
  }

  @SuppressWarnings("unchecked")
  public <Source> void simple(
      final TypeMatcher matcher, final Supplier<StreamEncoder<Target, Source>> supplier
  ) {
    this.streamEncoders.add(new StreamEncoderPair<>(matcher, (resolver, type, decoder) -> {
      return Stream.of((StreamEncoder<Target, Object>) supplier.get());
    }));
  }

  public <Source> void setup(
      final TypeMatcher matcher, final StreamEncoderBuilder<Target, Source> encoder
  ) {
    this.streamEncoders.add(new StreamEncoderPair<>(matcher, encoder));
  }

  @SuppressWarnings("unchecked")
  public <Source> Stream<StreamEncoder<Target, Source>> newStreamEncoder(
      final EntityResolver resolver, final JavaType type,
      final StreamEncoderFactory<Target> encoding
  ) {
    return streamEncoders.stream().filter(p -> p.matcher.matches(type)).flatMap(p -> {
      final Stream<? extends StreamEncoder<Target, ?>> stream =
          p.encoder.apply(resolver, type, encoding);
      return (Stream<StreamEncoder<Target, Source>>) stream;
    });
  }

  @Data
  static class StreamEncoderPair<T, O> {
    private final TypeMatcher matcher;
    private final StreamEncoderBuilder<T, O> encoder;
  }

  @FunctionalInterface
  public interface StreamEncoderBuilder<T, O> {
    Stream<StreamEncoder<T, O>> apply(
        EntityResolver resolver, JavaType type, StreamEncoderFactory<T> encoding
    );
  }
}
