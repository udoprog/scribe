package eu.toolchain.scribe;

import eu.toolchain.scribe.detector.InstanceBuilderDetector;
import eu.toolchain.scribe.detector.Match;
import eu.toolchain.scribe.detector.MatchPriority;
import eu.toolchain.scribe.reflection.Annotations;
import eu.toolchain.scribe.reflection.JavaType;
import lombok.Data;

import java.lang.annotation.Annotation;
import java.lang.annotation.RetentionPolicy;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

@Data
public class StaticMethodInstanceBuilder<Source> implements InstanceBuilder<Source> {
  private final List<EntityField> fields;
  private final Optional<List<String>> fieldNames;
  private final JavaType.Method method;

  @SuppressWarnings("unchecked")
  @Override
  public Source newInstance(final Context path, final List<Object> arguments) {
    try {
      return (Source) method.invoke(null, arguments.toArray());
    } catch (final Exception e) {
      throw path.error("failed to create instance using static method (" + method + ")", e);
    }
  }

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

        return new StaticMethodInstanceBuilder<>(fields, Optional.empty(), m);
      });
    }).map(Match.withPriority(MatchPriority.HIGH));
  }
}
