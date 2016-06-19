package eu.toolchain.ogt.fieldreader;

import com.google.common.base.CaseFormat;
import com.google.common.base.Converter;
import eu.toolchain.ogt.Annotations;
import eu.toolchain.ogt.Match;
import eu.toolchain.ogt.Methods;
import eu.toolchain.ogt.Priority;
import eu.toolchain.ogt.Reflection;
import lombok.Data;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Optional;
import java.util.stream.Stream;

@Data
public class GetterFieldReader implements FieldReader {
    private static final Converter<String, String> LOWER_TO_UPPER =
        CaseFormat.LOWER_CAMEL.converterTo(CaseFormat.UPPER_CAMEL);

    private final Method getter;
    private final Annotations annotations;
    private final Type fieldType;

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
    public Type fieldType() {
        return fieldType;
    }

    static boolean isBoolean(final Type type) {
        return type == Boolean.TYPE || type == Boolean.class;
    }

    public static Stream<Match<FieldReader>> detect(
        final Type type, final String fieldName, final Optional<Type> knownType
    ) {
        return Reflection.asClass(type).flatMap(c -> {
            final Methods methods = Methods.of(c.getDeclaredMethods());

            final String getterName = knownType.map(returnType -> {
                if (isBoolean(returnType)) {
                    return "is" + LOWER_TO_UPPER.convert(fieldName);
                }

                return "get" + LOWER_TO_UPPER.convert(fieldName);
            }).orElseGet(() -> "get" + LOWER_TO_UPPER.convert(fieldName));

            return methods.getMethods(getterName).map(getter -> {
                final Type returnType = getter.getGenericReturnType();
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
            });
        }).map(Match.withPriority(Priority.LOW));
    }
}
