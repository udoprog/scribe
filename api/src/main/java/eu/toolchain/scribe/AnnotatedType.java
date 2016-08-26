package eu.toolchain.scribe;

import java.lang.annotation.Annotation;
import java.util.stream.Stream;

public interface AnnotatedType {
  default <A extends Annotation> Stream<A> getAnnotation(
      final java.lang.Class<A> annotation
  ) {
    return getAnnotationStream().filter(annotation::isInstance).map(annotation::cast);
  }

  default <A extends Annotation> boolean isAnnotationPresent(
      final java.lang.Class<A> annotation
  ) {
    return getAnnotation(annotation).findFirst().isPresent();
  }

  Stream<Annotation> getAnnotationStream();
}
