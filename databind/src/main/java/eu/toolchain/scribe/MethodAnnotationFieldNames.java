package eu.toolchain.scribe;

import eu.toolchain.scribe.detector.FieldNameDetector;
import eu.toolchain.scribe.detector.Match;
import eu.toolchain.scribe.detector.MatchPriority;
import lombok.Data;

import java.lang.annotation.Annotation;
import java.util.function.Function;

@Data
public class MethodAnnotationFieldNames {
  public static <A extends Annotation> FieldNameDetector forAnnotation(
      Class<A> marker, final Function<A, String[]> names
  ) {
    return (resolver, type, annotations, index) -> annotations.getAnnotation(marker).map(a -> {
      final String[] values = names.apply(a);

      if (index >= values.length) {
        throw new IllegalArgumentException(
            "Number of values in annotation @" + marker.getName() + " is less than the required " +
                index);
      }

      return values[index];
    }).map(Match.withPriority(MatchPriority.DEFAULT));
  }
}
