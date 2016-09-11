package eu.toolchain.scribe;

import eu.toolchain.scribe.detector.InstanceBuilderDetector;
import eu.toolchain.scribe.detector.Match;
import eu.toolchain.scribe.detector.MatchPriority;
import eu.toolchain.scribe.reflection.AccessibleType;
import eu.toolchain.scribe.reflection.Annotations;
import eu.toolchain.scribe.reflection.JavaType;
import lombok.Data;

import java.lang.annotation.Annotation;
import java.lang.annotation.RetentionPolicy;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

@Data
public class ConstructorInstanceBuilder<Source> implements InstanceBuilder<Source> {
  private final List<EntityField> fields;
  private final Optional<List<String>> fieldNames;
  private final JavaType.Constructor constructor;

  @SuppressWarnings("unchecked")
  @Override
  public Source newInstance(final Context path, final List<Object> arguments) {
    try {
      return (Source) constructor.newInstance(arguments.toArray());
    } catch (final Exception e) {
      throw path.error("failed to create instance using constructor (" + constructor + ")", e);
    }
  }

  public static InstanceBuilderDetector forEmpty() {
    return (resolver, type) -> type
        .getConstructors()
        .filter(AccessibleType::isPublic)
        .filter(c -> c.getParameters().isEmpty())
        .map(c -> new ConstructorInstanceBuilder<>(Collections.emptyList(), Optional.empty(), c))
        .map(Match.withPriority(MatchPriority.LOW));
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

    return (resolver, type) -> type.getConstructors().flatMap(c -> {
      if (!c.isPublic()) {
        return Stream.of();
      }

      return c.getAnnotation(marker).map(a -> {
        final List<EntityField> fields = resolver.detectExecutableFields(c);
        final Optional<List<String>> fieldNames = names.apply(a);

        fieldNames.ifPresent(n -> {
          if (n.size() != fields.size()) {
            throw new IllegalArgumentException(
                "Constructor (" + c + ") unexpected number forAnnotation fields (" + fields.size() +
                    "), expected (" + n.size() + ") due to value in " + a + " annotation");
          }
        });

        return new ConstructorInstanceBuilder<>(fields, fieldNames, c);
      });
    }).map(Match.withPriority(MatchPriority.HIGH));
  }
}
