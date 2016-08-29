package eu.toolchain.scribe.typemapper;

import eu.toolchain.scribe.EntityResolver;
import eu.toolchain.scribe.JavaType;
import eu.toolchain.scribe.Match;
import eu.toolchain.scribe.MatchPriority;
import eu.toolchain.scribe.TypeMatcher;
import eu.toolchain.scribe.typemapping.TypeMapping;

import java.util.function.Function;
import java.util.stream.Stream;

@FunctionalInterface
public interface TypeMapper {
  Stream<Match<TypeMapping>> map(
      final EntityResolver resolver, final JavaType type
  );

  static TypeMapper matchMapper(
      final TypeMatcher matcher, final Function<JavaType, TypeMapping> mapping
  ) {
    return (resolver, type) -> {
      final Stream<TypeMapping> stream =
          matcher.matches(type) ? Stream.of(mapping.apply(type)) : Stream.empty();

      return stream.map(Match.withPriority(MatchPriority.HIGH));
    };
  }
}
