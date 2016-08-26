package eu.toolchain.scribe.fieldreader;

import eu.toolchain.scribe.Annotations;
import eu.toolchain.scribe.JavaType;
import eu.toolchain.scribe.Match;
import eu.toolchain.scribe.MatchPriority;
import eu.toolchain.scribe.entitymapper.FieldReaderDetector;
import lombok.Data;

import java.lang.annotation.Annotation;
import java.util.function.Function;

@Data
public class AnnotatedFieldReader implements FieldReader {
  private final JavaType.Method getter;
  private final Annotations annotations;
  private final JavaType fieldType;

  @Override
  public Object read(Object instance) {
    try {
      return getter.invoke(instance);
    } catch (final Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public Annotations annotations() {
    return annotations;
  }

  @Override
  public JavaType fieldType() {
    return fieldType;
  }

  public static <T extends Annotation> FieldReaderDetector forAnnotation(
      final Class<T> annotation, final Function<T, String> annotationValue
  ) {
    return (type, fieldName, knownType) -> type
        .findByAnnotation(JavaType::getMethods, annotation)
        .filter(m -> annotationValue
            .apply(m.getAnnotation(annotation).findFirst().get())
            .equals(fieldName))
        .map(m -> {
          final JavaType fieldType = m.getReturnType();

          if (!knownType.equals(fieldType)) {
            throw new IllegalArgumentException(
                "Getter " + m + " return incompatible return value (" +
                    fieldType +
                    "), expected (" + knownType + ")");
          }

          final Annotations annotations = Annotations.of(m.getAnnotationStream());
          return new AnnotatedFieldReader(m, annotations, fieldType);
        })
        .map(Match.withPriority(MatchPriority.HIGH));
  }
}
