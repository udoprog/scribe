package eu.toolchain.scribe;

import eu.toolchain.scribe.detector.FieldReaderDetector;
import eu.toolchain.scribe.detector.Match;
import eu.toolchain.scribe.detector.MatchPriority;
import eu.toolchain.scribe.reflection.Annotations;
import eu.toolchain.scribe.reflection.JavaType;
import lombok.Data;

import java.util.function.BiFunction;
import java.util.function.Function;

@Data
public class GetterFieldReader implements FieldReader {
  public static final Function<String, String> LOWER_TO_UPPER = CaseFormat::lowerCamelToUpperCamel;

  private final JavaType.Method getter;
  private final Annotations annotations;
  private final JavaType fieldType;

  @Override
  public Object read(final Context path, final Object instance) {
    try {
      return getter.invoke(instance);
    } catch (final Exception e) {
      throw path.error(e);
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

  public static FieldReaderDetector forBeanGetter() {
    return forName(GetterFieldReader::beanGetterName);
  }

  public static FieldReaderDetector forName(final BiFunction<JavaType, String, String> name) {
    return (type, fieldName, fieldType) -> {
      final String getterName = name.apply(fieldType, fieldName);

      return type.getMethod(getterName).map(getter -> {
        final JavaType returnType = getter.getReturnType();
        final Annotations annotations = Annotations.of(getter.getAnnotationStream());

        if (!fieldType.equals(returnType)) {
          throw new IllegalArgumentException(
              "Getter " + getter + " return incompatible return value (" + returnType +
                  "), expected (" + fieldType + ")");
        }

        return new GetterFieldReader(getter, annotations, returnType);
      }).map(Match.withPriority(MatchPriority.DEFAULT));
    };
  }

  private static String beanGetterName(final JavaType fieldType, final String fieldName) {
    if (fieldType.isBoolean() && !fieldType.isBoxed()) {
      return "is" + LOWER_TO_UPPER.apply(fieldName);
    }

    return "get" + LOWER_TO_UPPER.apply(fieldName);
  }
}
