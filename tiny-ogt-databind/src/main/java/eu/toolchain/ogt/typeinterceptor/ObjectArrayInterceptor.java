package eu.toolchain.ogt.typeinterceptor;

import eu.toolchain.ogt.Annotations;
import eu.toolchain.ogt.EntityResolver;
import eu.toolchain.ogt.JavaType;
import eu.toolchain.ogt.type.ObjectArrayTypeMapping;
import eu.toolchain.ogt.type.TypeMapping;

import java.util.Optional;

public class ObjectArrayInterceptor {
    public static Optional<TypeMapping> intercept(
        final EntityResolver resolver, final JavaType type, final Annotations annotations
    ) {
        if (!type.getRawClass().isArray()) {
            return Optional.empty();
        }

        final TypeMapping component = resolver.mapping(type.getRawClass().getComponentType());
        return Optional.of(new ObjectArrayTypeMapping(type, component));
    }
}
