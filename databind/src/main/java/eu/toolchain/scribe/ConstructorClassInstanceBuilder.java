package eu.toolchain.scribe;

import eu.toolchain.scribe.detector.InstanceBuilderDetector;
import eu.toolchain.scribe.detector.Match;
import eu.toolchain.scribe.detector.MatchPriority;
import eu.toolchain.scribe.reflection.AccessibleType;
import eu.toolchain.scribe.reflection.Annotations;
import lombok.Data;

import java.lang.annotation.Annotation;
import java.lang.annotation.RetentionPolicy;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

@Data
public class ConstructorClassInstanceBuilder<Source> implements ClassInstanceBuilder<Source> {
  private final List<EntityField> fields;
  private final InstanceBuilder<Source> instanceBuilder;

  public static InstanceBuilderDetector forEmpty() {
    return (resolver, type) -> type
        .getConstructors()
        .filter(AccessibleType::isPublic)
        .filter(c -> c.getParameters().isEmpty())
        .map(c -> {
          final InstanceBuilder<Object> instanceBuilder = InstanceBuilder.fromConstructor(c);
          return new ConstructorClassInstanceBuilder<>(Collections.emptyList(), instanceBuilder);
        })
        .map(Match.withPriority(MatchPriority.DEFAULT));
  }

  public static <A extends Annotation> InstanceBuilderDetector forAnnotation(
      final Class<A> marker
  ) {
    Annotations.verifyRetentionPolicy(marker, RetentionPolicy.RUNTIME);

    return (resolver, type) -> type.getConstructors().flatMap(c -> {
      if (!c.isPublic()) {
        return Stream.of();
      }

      return c.getAnnotation(marker).map(a -> {
        final List<EntityField> fields = resolver.detectExecutableFields(c);

        final InstanceBuilder<Object> instanceBuilder = InstanceBuilder.fromConstructor(c);
        return new ConstructorClassInstanceBuilder<>(fields, instanceBuilder);
      });
    }).map(Match.withPriority(MatchPriority.HIGH));
  }
}
