package eu.toolchain.scribe.entitymapper;

import eu.toolchain.scribe.Annotations;
import eu.toolchain.scribe.JavaType;
import eu.toolchain.scribe.TypeMatcher;
import eu.toolchain.scribe.typealias.TypeAlias;

import java.util.function.Function;
import java.util.stream.Stream;

@FunctionalInterface
public interface TypeAliasDetector {
  Stream<TypeAlias> apply(JavaType type, Annotations annotations);

  static TypeAliasDetector matchAlias(
      final TypeMatcher matcher, final Function<JavaType, TypeAlias> mapping
  ) {
    return (type, annotations) -> (matcher.matches(type) ? Stream.of(mapping.apply(type))
        : Stream.empty());
  }
}
