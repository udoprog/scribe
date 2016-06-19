package eu.toolchain.ogt.subtype;

import eu.toolchain.ogt.Match;
import eu.toolchain.ogt.Priority;
import eu.toolchain.ogt.entitymapper.SubTypesDetector;
import eu.toolchain.ogt.type.JavaType;
import eu.toolchain.ogt.typemapping.EntityTypeMapping;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.Collectors;

public class AnnotationSubTypesResolver {
    public static <A extends Annotation, T extends Annotation> SubTypesDetector forAnnotation(
        Class<A> annotation, Function<A, T[]> value, Function<T, JavaType> valueAsType
    ) {
        return (resolver, type) -> type
            .getAnnotation(annotation)
            .map(a -> Arrays
                .stream(value.apply(a))
                .map(valueAsType)
                .map(resolver::mapping)
                .map(m -> {
                    if (!(m instanceof EntityTypeMapping)) {
                        throw new IllegalArgumentException("Not an entity mapping: " + m);
                    }

                    return (EntityTypeMapping) m;
                })
                .collect(Collectors.toList()))
            .map(Match.withPriority(Priority.HIGH));
    }
}
