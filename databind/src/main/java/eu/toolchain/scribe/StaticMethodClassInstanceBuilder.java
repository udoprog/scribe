package eu.toolchain.scribe;

import eu.toolchain.scribe.detector.InstanceBuilderDetector;
import eu.toolchain.scribe.detector.Match;
import eu.toolchain.scribe.detector.MatchPriority;
import eu.toolchain.scribe.reflection.Annotations;
import lombok.Data;

import java.lang.annotation.Annotation;
import java.lang.annotation.RetentionPolicy;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

@Data
public class StaticMethodClassInstanceBuilder<Source> implements ClassInstanceBuilder<Source> {
  private final List<EntityField> fields;
  private final Optional<List<String>> fieldNames;
  private final InstanceBuilder.StaticMethod<Source> instanceBuilder;

  public static <A extends Annotation> InstanceBuilderDetector forAnnotation(
      final Class<A> marker
  ) {
    return forAnnotation(marker, a -> Optional.empty());
  }

  public static <A extends Annotation> InstanceBuilderDetector forAnnotation(
      final Class<A> marker, final Function<A, Optional<List<String>>> names
  ) {
    Annotations.verifyRetentionPolicy(marker, RetentionPolicy.RUNTIME);

    return (resolver, type) -> type.getMethods().flatMap(m -> {
      if (!m.isPublic() || !m.isStatic()) {
        return Stream.of();
      }

      return m.getAnnotation(marker).map(a -> {
        final List<EntityField> fields = resolver.detectExecutableFields(m);
        final Optional<List<String>> fieldNames = names.apply(a);

        if (!type.equals(m.getReturnType())) {
          throw new IllegalArgumentException(
              String.format("@%s method must return (%s): %s", marker, type, m));
        }

        fieldNames.ifPresent(n -> {
          if (n.size() != fields.size()) {
            throw new IllegalArgumentException(
                "Method (" + m + ") has unexpected number forAnnotation fields (" + fields.size() +
                    "), expected (" + n.size() + ") due to value in " + a + " annotation");
          }
        });

        final InstanceBuilder.StaticMethod<Object> instanceBuilder =
            InstanceBuilder.fromStaticMethod(m);
        return new StaticMethodClassInstanceBuilder<>(fields, Optional.empty(), instanceBuilder);
      });
    }).map(Match.withPriority(MatchPriority.HIGH));
  }
}
