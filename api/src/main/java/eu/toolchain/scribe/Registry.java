package eu.toolchain.scribe;

import eu.toolchain.scribe.reflection.JavaType;

import java.util.stream.Stream;

public class Registry<T, Factory> {
  private final MatcherRegistry<Builder<T, Factory>> registry = new MatcherRegistry<>();

  public void constant(final TypeMatcher matcher, final T value) {
    registry.add(matcher, (resolver, type, decoder) -> Stream.of(value));
  }

  public void setup(final TypeMatcher matcher, final Builder<T, Factory> builder) {
    registry.add(matcher, builder);
  }

  @SuppressWarnings("unchecked")
  public Stream<? extends T> newInstance(
      final EntityResolver resolver, final JavaType type, final Factory factory
  ) {
    return registry.find(type).flatMap(p -> p.apply(resolver, type, factory));
  }

  @FunctionalInterface
  public interface Builder<T, Factory> {
    Stream<T> apply(EntityResolver resolver, JavaType type, Factory factory);
  }
}
