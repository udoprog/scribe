package eu.toolchain.scribe;

import eu.toolchain.scribe.detector.Match;
import eu.toolchain.scribe.detector.MatchPriority;
import eu.toolchain.scribe.detector.SubTypesDetector;
import eu.toolchain.scribe.reflection.JavaType;

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
          final Mapping<Object> m = resolver.mapping(valueAsType.apply(v));

          if (!(m instanceof ClassMapping)) {
            throw new IllegalArgumentException("Not an entity mapping: " + m);
          }

          return new SubType<>((ClassMapping<Object>) m, fieldName.apply(v));
        }).collect(Collectors.toList()))
        .map(Match.withPriority(MatchPriority.HIGH));
  }
}
