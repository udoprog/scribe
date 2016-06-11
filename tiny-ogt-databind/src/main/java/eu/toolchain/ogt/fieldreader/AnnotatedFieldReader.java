package eu.toolchain.ogt.fieldreader;

import eu.toolchain.ogt.Annotations;
import eu.toolchain.ogt.JavaType;
import eu.toolchain.ogt.Reflection;
import eu.toolchain.ogt.entitymapper.FieldReaderDetector;
import lombok.Data;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.function.Function;

@Data
public class AnnotatedFieldReader implements FieldReader {
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
        return "AnnotatedFieldReader(" + getter.toString() + ")";
    }

    public static <T extends Annotation> FieldReaderDetector of(
        final Class<T> annotation, final Function<T, String> annotationValue
    ) {
        return (type, fieldName, knownType) -> Reflection
            .findAnnotatedMethods(type, annotation)
            .filter(m -> annotationValue.apply(m.getAnnotation(annotation)).equals(fieldName))
            .findFirst()
            .map(m -> {
                final JavaType fieldType = JavaType.construct(m.getGenericReturnType());

                knownType.ifPresent(expected -> {
                    if (!expected.equals(fieldType)) {
                        throw new IllegalArgumentException(
                            "Getter " + m + " return incompatible return value (" +
                                fieldType +
                                "), expected (" + expected + ")");
                    }
                });

                final Annotations annotations = Annotations.of(m.getAnnotations());
                return new AnnotatedFieldReader(m, annotations, fieldType);
            });
    }
}
