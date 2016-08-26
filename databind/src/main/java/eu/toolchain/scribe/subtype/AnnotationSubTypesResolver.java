package eu.toolchain.scribe.subtype;

import eu.toolchain.scribe.JavaType;
import eu.toolchain.scribe.Match;
import eu.toolchain.scribe.MatchPriority;
import eu.toolchain.scribe.entitymapper.SubType;
import eu.toolchain.scribe.entitymapper.SubTypesDetector;
import eu.toolchain.scribe.typemapping.EntityTypeMapping;
import eu.toolchain.scribe.typemapping.TypeMapping;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public class AnnotationSubTypesResolver {
  public static <A extends Annotation, T extends Annotation> SubTypesDetector forAnnotation(
      Class<A> annotation, Function<A, T[]> value, Function<T, JavaType> valueAsType,
      Function<T, Optional<String>> fieldName
  ) {
    return (resolver, type) -> type
        .getAnnotation(annotation)
        .map(a -> Arrays.stream(value.apply(a)).map(v -> {
          final TypeMapping m = resolver.mapping(valueAsType.apply(v));

          if (!(m instanceof EntityTypeMapping)) {
            throw new IllegalArgumentException("Not an entity mapping: " + m);
          }

          return new SubType((EntityTypeMapping) m, fieldName.apply(v));
        }).collect(Collectors.toList()))
        .map(Match.withPriority(MatchPriority.HIGH));
  }
}
