package eu.toolchain.ogt.fieldreader;

import com.google.common.base.CaseFormat;
import com.google.common.base.Converter;
import eu.toolchain.ogt.Annotations;
import eu.toolchain.ogt.Match;
import eu.toolchain.ogt.Priority;
import eu.toolchain.ogt.type.JavaType;
import lombok.Data;

import java.util.Optional;
import java.util.stream.Stream;

@Data
public class GetterFieldReader implements FieldReader {
    private static final Converter<String, String> LOWER_TO_UPPER =
        CaseFormat.LOWER_CAMEL.converterTo(CaseFormat.UPPER_CAMEL);

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
        final JavaType type, final String fieldName, final Optional<JavaType> knownType
    ) {
        final String getterName = knownType.map(returnType -> {
            if (returnType.isBoolean()) {
                return "is" + LOWER_TO_UPPER.convert(fieldName);
            }

            return "get" + LOWER_TO_UPPER.convert(fieldName);
        }).orElseGet(() -> "get" + LOWER_TO_UPPER.convert(fieldName));

        return type.getMethod(getterName).map(getter -> {
            final JavaType returnType = getter.getReturnType();
            final Annotations annotations = Annotations.of(getter.getAnnotations());

            knownType.ifPresent(expected -> {
                if (!expected.equals(returnType)) {
                    throw new IllegalArgumentException(
                        "Getter " + getter + " return incompatible return value (" +
                            returnType +
                            "), expected (" + expected + ")");
                }
            });

            return new GetterFieldReader(getter, annotations, returnType);
        }).map(Match.withPriority(Priority.LOW));
    }
}
