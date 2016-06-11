package eu.toolchain.ogt.typeinterceptor;

import eu.toolchain.ogt.Annotations;
import eu.toolchain.ogt.EntityResolver;
import eu.toolchain.ogt.JavaType;
import eu.toolchain.ogt.entitymapper.TypeInterceptor;
import eu.toolchain.ogt.type.TypeMapping;
import lombok.RequiredArgsConstructor;

import java.util.Optional;
import java.util.function.Function;

@RequiredArgsConstructor
public class RawInterceptor implements TypeInterceptor {
    private final Class<?> raw;
    private final Function<JavaType, TypeMapping> converter;

    @Override
    public Optional<TypeMapping> intercept(
        final EntityResolver resolver, final JavaType type, final Annotations annotations
    ) {
        if (raw.isAssignableFrom(type.getRawClass())) {
            return Optional.of(converter.apply(type));
        }

        return Optional.empty();
    }

    public static RawInterceptor of(
        final Class<?> raw, Function<JavaType, TypeMapping> converter
    ) {
        return new RawInterceptor(raw, converter);
    }
}
