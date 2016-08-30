package eu.toolchain.scribe.reflection;

import lombok.Data;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
public class Annotations {
  private final List<Annotation> annotations;

  public boolean isAnnotationPresent(final Class<? extends Annotation> annotation) {
    return annotations
        .stream()
        .filter(a -> annotation.isAssignableFrom(a.getClass()))
        .findFirst()
        .isPresent();
  }

  public <T extends Annotation> Stream<T> getAnnotation(final Class<T> annotation) {
    return annotations
        .stream()
        .filter(a -> annotation.isAssignableFrom(a.getClass()))
        .map(annotation::cast);
  }

  public static Annotations of(final Annotation... annotations) {
    return new Annotations(Stream.of(annotations).collect(Collectors.toList()));
  }

  public static Annotations of(final Stream<Annotation> annotations) {
    return new Annotations(annotations.collect(Collectors.toList()));
  }

  public static Annotations empty() {
    return new Annotations(Collections.emptyList());
  }

  public Annotations merge(final Annotations a) {
    final HashSet<Annotation> annotations = new HashSet<>(this.annotations);
    annotations.addAll(a.annotations);
    return new Annotations(new ArrayList<>(annotations));
  }

  /**
   * Helper method to verify that a given annotation class has a specific retention policy.
   * <p>
   * This is typically needed because a {@code RUNTIME}
   * {@link java.lang.annotation.RetentionPolicy}
   * is required for accessing an annotation reflexively.
   *
   * @param annotation Annotation to check.
   * @param policy Retention policy to verify.
   */
  public static void verifyRetentionPolicy(
      final Class<? extends Annotation> annotation, RetentionPolicy policy
  ) {
    final Retention retention = annotation.getAnnotation(Retention.class);

    if (retention == null || retention.value() != policy) {
      throw new IllegalArgumentException(
          "Annotation (" + annotation + ") does not have a " + policy + " retention policy");
    }
  }
}
