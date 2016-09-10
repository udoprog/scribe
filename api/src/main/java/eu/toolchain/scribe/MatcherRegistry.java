package eu.toolchain.scribe;

import eu.toolchain.scribe.reflection.JavaType;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class MatcherRegistry<T> {
  private final List<Entry<T>> entries = new ArrayList<>();

  public void add(final TypeMatcher matcher, final T value) {
    entries.add(new Entry<>(matcher, value));
  }

  public Stream<T> find(final JavaType type) {
    return entries.stream().filter(e -> e.matcher.matches(type)).map(Entry::getValue);
  }

  @Data
  static class Entry<T> {
    private final TypeMatcher matcher;
    private final T value;
  }

  @FunctionalInterface
  public interface DecoderBuilder<Target, TargetEntity, Source> {
    Stream<Decoder<Target, Source>> apply(
        EntityResolver resolver, JavaType type, DecoderFactory<Target, TargetEntity> decoder
    );
  }
}
