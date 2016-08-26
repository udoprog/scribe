package eu.toolchain.scribe.fieldreader;

import eu.toolchain.scribe.Annotations;
import eu.toolchain.scribe.CaseFormat;
import eu.toolchain.scribe.JavaType;
import eu.toolchain.scribe.Match;
import eu.toolchain.scribe.MatchPriority;
import lombok.Data;

import java.util.function.Function;
import java.util.stream.Stream;

@Data
public class GetterFieldReader implements FieldReader {
  public static final Function<String, String> LOWER_TO_UPPER = CaseFormat::lowerCamelToUpperCamel;

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

  public static Stream<Match<FieldReader>> detect(
      final JavaType type, final String fieldName, final JavaType fieldType
  ) {
    final String getterName = getFieldName(fieldName, fieldType);

    return type.getMethod(getterName).map(getter -> {
      final JavaType returnType = getter.getReturnType();
      final Annotations annotations = Annotations.of(getter.getAnnotationStream());

      if (!fieldType.equals(returnType)) {
        throw new IllegalArgumentException(
            "Getter " + getter + " return incompatible return value (" +
                returnType +
                "), expected (" + fieldType + ")");
      }

      return new GetterFieldReader(getter, annotations, returnType);
    }).map(Match.withPriority(MatchPriority.LOW));
  }

  private static String getFieldName(final String fieldName, final JavaType fieldType) {
    if (fieldType.isBoolean() && !fieldType.isBoxed()) {
      return "is" + LOWER_TO_UPPER.apply(fieldName);
    }

    return "get" + LOWER_TO_UPPER.apply(fieldName);
  }
}
