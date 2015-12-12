package eu.toolchain.ogt.fieldreader;

import com.google.common.base.CaseFormat;
import com.google.common.base.Converter;

import java.lang.reflect.Method;
import java.util.Optional;

import eu.toolchain.ogt.JavaType;
import eu.toolchain.ogt.PrimitiveType;
import lombok.Data;

@Data
public class GetterFieldReader implements FieldReader {
    private static final Converter<String, String> LOWER_TO_UPPER =
            CaseFormat.LOWER_CAMEL.converterTo(CaseFormat.UPPER_CAMEL);

    private final Method getter;

    @Override
    public Object read(Object instance) {
        try {
            return getter.invoke(instance);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String toString() {
        return "GetterFieldReader(" + getter.toString() + ")";
    }

    public static Optional<FieldReader> detect(final JavaType type, final JavaType returnType,
            final String fieldName) {
        final String getterName = PrimitiveType.detect(returnType).flatMap(primitive -> {
            if (primitive == PrimitiveType.BOOLEAN) {
                return Optional.of("is" + LOWER_TO_UPPER.convert(fieldName));
            }

            return Optional.empty();
        }).orElseGet(() -> "get" + LOWER_TO_UPPER.convert(fieldName));

        final Method getter;

        try {
            getter = type.getRawClass().getMethod(getterName);
        } catch (final NoSuchMethodException e) {
            return Optional.empty();
        } catch (final Exception e) {
            throw new IllegalArgumentException("Could not access getter for field (" + fieldName
                    + "), expected " + type + "#" + getterName + "()", e);
        }

        final JavaType actualReturnType = JavaType.construct(getter.getGenericReturnType());

        if (!returnType.equals(actualReturnType)) {
            throw new IllegalArgumentException(
                    "Getter " + getter + " return incompatible return value (" + actualReturnType
                            + "), expected (" + returnType + ")");
        }

        return Optional.of(new GetterFieldReader(getter));
    }
}
