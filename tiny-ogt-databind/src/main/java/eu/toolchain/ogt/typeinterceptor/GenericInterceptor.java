package eu.toolchain.ogt.typeinterceptor;

import eu.toolchain.ogt.Annotations;
import eu.toolchain.ogt.EntityResolver;
import eu.toolchain.ogt.JavaType;
import eu.toolchain.ogt.entitymapper.TypeInterceptor;
import eu.toolchain.ogt.type.TypeMapping;
import lombok.RequiredArgsConstructor;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.stream.IntStream;

@RequiredArgsConstructor
public class GenericInterceptor implements TypeInterceptor {
    private final Class<?> raw;
    private final int count;
    private final BiFunction<JavaType, TypeMapping[], TypeMapping> converter;

    @Override
    public Optional<TypeMapping> intercept(
        final EntityResolver resolver, final JavaType type, final Annotations annotations
    ) {
        if (raw.isAssignableFrom(type.getRawClass()) && type.getParameterCount() == count) {

            final TypeMapping[] mappings = IntStream
                .range(0, count)
                .mapToObj(type::getContainedType)
                .map(resolver::mapping)
                .toArray(TypeMapping[]::new);

            return Optional.of(converter.apply(type, mappings));
        }

        return Optional.empty();
    }

    public static GenericInterceptor of(
        final Class<?> raw, int parameterCount,
        BiFunction<JavaType, TypeMapping[], TypeMapping> converter
    ) {
        return new GenericInterceptor(raw, parameterCount, converter);
    }
}
