package eu.toolchain.scribe.detector;

import eu.toolchain.scribe.TypeAlias;
import eu.toolchain.scribe.TypeMatcher;
import eu.toolchain.scribe.reflection.Annotations;
import eu.toolchain.scribe.reflection.JavaType;

import java.util.function.Function;
import java.util.stream.Stream;

/**
 * A detector for type aliases.
 */
@FunctionalInterface
public interface TypeAliasDetector {
  Stream<TypeAlias> detect(JavaType type, Annotations annotations);

  /**
   * Setup a simple type alias detector.
   *
   * @param matcher Matcher for the type to match.
   * @param mapping Function that creates the type alias, if the matcher matches.
   * @return A type alias detector for the given matcher.
   */
  static TypeAliasDetector matchAlias(
      final TypeMatcher matcher, final Function<JavaType, TypeAlias> mapping
  ) {
    return (type, annotations) -> {
      return matcher.matches(type) ? Stream.of(mapping.apply(type)) : Stream.empty();
    };
  }
}
