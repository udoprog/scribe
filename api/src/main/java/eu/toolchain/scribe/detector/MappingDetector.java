package eu.toolchain.scribe.detector;

import eu.toolchain.scribe.EntityResolver;
import eu.toolchain.scribe.Mapping;
import eu.toolchain.scribe.TypeMatcher;
import eu.toolchain.scribe.reflection.JavaType;

import java.util.function.Function;
import java.util.stream.Stream;

@FunctionalInterface
public interface MappingDetector {
  Stream<Match<Mapping>> map(EntityResolver resolver, JavaType type);

  static MappingDetector matchMapping(
      final TypeMatcher matcher, final Function<JavaType, Mapping> mapping
  ) {
    return (resolver, type) -> {
      final Stream<Mapping> stream =
          matcher.matches(type) ? Stream.of(mapping.apply(type)) : Stream.empty();

      return stream.map(Match.withPriority(MatchPriority.HIGH));
    };
  }
}
