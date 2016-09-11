package eu.toolchain.scribe.detector;

import eu.toolchain.scribe.EntityResolver;
import eu.toolchain.scribe.Mapping;
import eu.toolchain.scribe.TypeMatcher;
import eu.toolchain.scribe.reflection.JavaType;

import java.util.function.Function;
import java.util.stream.Stream;

@FunctionalInterface
public interface MappingDetector {
  Stream<Match<Mapping<Object>>> detect(EntityResolver resolver, JavaType type);

  static MappingDetector matchMapping(
      final TypeMatcher matcher, final Function<JavaType, Mapping<Object>> mapping
  ) {
    return (resolver, type) -> {
      final Stream<Mapping<Object>> stream =
          matcher.matches(type) ? Stream.of(mapping.apply(type)) : Stream.empty();

      return stream.map(Match.withPriority(MatchPriority.HIGH));
    };
  }
}
