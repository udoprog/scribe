package eu.toolchain.ogt.fieldreader;

import com.google.common.base.CaseFormat;
import com.google.common.base.Converter;
import com.google.common.collect.ImmutableList;
import eu.toolchain.ogt.Annotations;
import eu.toolchain.ogt.JavaType;
import eu.toolchain.ogt.PrimitiveType;
import lombok.Data;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;

@Data
public class GetterFieldReader implements FieldReader {
    private static final Converter<String, String> LOWER_TO_UPPER =
        CaseFormat.LOWER_CAMEL.converterTo(CaseFormat.UPPER_CAMEL);

    private final Method getter;
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

    @Override
    public String toString() {
        return "GetterFieldReader(" + getter.toString() + ")";
    }

    public static Optional<FieldReader> detect(
        final JavaType type, final String fieldName, final Optional<JavaType> knownType
    ) {
        final String getterName =
            knownType.map(returnType -> PrimitiveType.detect(returnType).flatMap(primitive -> {
                if (primitive == PrimitiveType.BOOLEAN) {
                    return Optional.of("is" + LOWER_TO_UPPER.convert(fieldName));
                }

                return Optional.empty();
            }).orElseGet(() -> "get" + LOWER_TO_UPPER.convert(fieldName))).orElseGet(() -> {
                final List<String> names =
                    ImmutableList.of("is" + LOWER_TO_UPPER.convert(fieldName),
                        "get" + LOWER_TO_UPPER.convert(fieldName));

                return names
                    .stream()
                    .filter(g -> {
                        final Method m;

                        try {
                            m = type.getRaw().getMethod(g);
                        } catch (final NoSuchMethodException e) {
                            return false;
                        }

                        // ignore methods returning void
                        return !JavaType.construct(m.getGenericReturnType()).isVoid();
                    })
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException(
                        String.format("Could not find accessor for field %s in type %s, tried: %s",
                            fieldName, type, names)));
            });

        final Method getter;

        try {
            getter = type.getRawClass().getMethod(getterName);
        } catch (final NoSuchMethodException e) {
            return Optional.empty();
        } catch (final Exception e) {
            throw new IllegalArgumentException(
                "Could not access getter for field (" + fieldName + "), expected " + type + "#" +
                    getterName + "()", e);
        }

        final JavaType fieldType = JavaType.construct(getter.getGenericReturnType());

        knownType.ifPresent(expected -> {
            if (!expected.equals(fieldType)) {
                throw new IllegalArgumentException(
                    "Getter " + getter + " return incompatible return value (" + fieldType +
                        "), expected (" + expected + ")");
            }
        });

        final Annotations annotations = Annotations.of(getter.getAnnotations());

        return Optional.of(new GetterFieldReader(getter, annotations, fieldType));
    }
}
