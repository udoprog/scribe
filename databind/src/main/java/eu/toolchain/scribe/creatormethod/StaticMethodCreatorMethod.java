package eu.toolchain.scribe.creatormethod;

import eu.toolchain.scribe.Annotations;
import eu.toolchain.scribe.EntityField;
import eu.toolchain.scribe.JavaType;
import eu.toolchain.scribe.Match;
import eu.toolchain.scribe.MatchPriority;
import eu.toolchain.scribe.entitymapper.CreatorMethodDetector;
import lombok.Data;

import java.lang.annotation.Annotation;
import java.lang.annotation.RetentionPolicy;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

@Data
public class StaticMethodCreatorMethod implements CreatorMethod {
  private final List<EntityField> fields;
  private final Optional<List<String>> fieldNames;
  private final JavaType.Method method;

  @Override
  public Object newInstance(final List<Object> arguments) {
    try {
      return method.invoke(null, arguments.toArray());
    } catch (final Exception e) {
      throw new RuntimeException(e);
    }
  }

  public static <A extends Annotation> CreatorMethodDetector forAnnotation(
      final Class<A> marker
  ) {
    return forAnnotation(marker, a -> Optional.empty());
  }

  public static <A extends Annotation> CreatorMethodDetector forAnnotation(
      final Class<A> marker, final Function<A, Optional<List<String>>> names
  ) {
    Annotations.verifyRetentionPolicy(marker, RetentionPolicy.RUNTIME);

    return (resolver, type) -> type.getMethods().flatMap(m -> {
      if (!m.isPublic() || !m.isStatic()) {
        return Stream.<CreatorMethod>of();
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
            throw new IllegalArgumentException("Method (" + m +
                ") has unexpected number forAnnotation fields (" + fields.size() +
                "), expected (" +
                n.size() + ") due to value in " + a + " annotation");
          }
        });

        return new StaticMethodCreatorMethod(fields, Optional.empty(), m);
      });
    }).map(Match.withPriority(MatchPriority.HIGH));
  }
}
