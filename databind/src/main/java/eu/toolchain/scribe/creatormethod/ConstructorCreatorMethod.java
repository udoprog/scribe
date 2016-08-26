package eu.toolchain.scribe.creatormethod;

import eu.toolchain.scribe.AccessibleType;
import eu.toolchain.scribe.Annotations;
import eu.toolchain.scribe.EntityField;
import eu.toolchain.scribe.JavaType;
import eu.toolchain.scribe.Match;
import eu.toolchain.scribe.MatchPriority;
import eu.toolchain.scribe.entitymapper.CreatorMethodDetector;
import lombok.Data;

import java.lang.annotation.Annotation;
import java.lang.annotation.RetentionPolicy;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

@Data
public class ConstructorCreatorMethod implements CreatorMethod {
  private final List<EntityField> fields;
  private final Optional<List<String>> fieldNames;
  private final JavaType.Constructor constructor;

  @Override
  public Object newInstance(final List<Object> arguments) {
    try {
      return constructor.newInstance(arguments.toArray());
    } catch (final Exception e) {
      throw new RuntimeException(e);
    }
  }

  public static CreatorMethodDetector forEmpty() {
    return (resolver, type) -> type
        .getConstructors()
        .filter(AccessibleType::isPublic)
        .filter(c -> c.getParameters().isEmpty())
        .map(c -> new ConstructorCreatorMethod(Collections.emptyList(), Optional.empty(), c))
        .map(Match.withPriority(MatchPriority.LOW));
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

    return (resolver, type) -> type.getConstructors().flatMap(c -> {
      if (!c.isPublic()) {
        return Stream.of();
      }

      return c.getAnnotation(marker).map(a -> {
        final List<EntityField> fields = resolver.detectExecutableFields(c);
        final Optional<List<String>> fieldNames = names.apply(a);

        fieldNames.ifPresent(n -> {
          if (n.size() != fields.size()) {
            throw new IllegalArgumentException("Constructor (" + c +
                ") unexpected number forAnnotation fields (" + fields.size() + "), expected (" +
                n.size() + ") due to value in " + a + " annotation");
          }
        });

        return new ConstructorCreatorMethod(fields, fieldNames, c);
      });
    }).map(Match.withPriority(MatchPriority.HIGH));
  }
}
