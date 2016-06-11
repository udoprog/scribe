package eu.toolchain.ogt.typeinterceptor;

import eu.toolchain.ogt.Annotations;
import eu.toolchain.ogt.EntityResolver;
import eu.toolchain.ogt.JavaType;
import eu.toolchain.ogt.type.ByteArrayTypeMapping;
import eu.toolchain.ogt.type.TypeMapping;

import java.util.Optional;

public class ByteArrayInterceptor {
    public static Optional<TypeMapping> intercept(
        final EntityResolver resolver, final JavaType type, final Annotations annotations
    ) {
        if (!byte[].class.isAssignableFrom(type.getRawClass())) {
            return Optional.empty();
        }

        return Optional.of(new ByteArrayTypeMapping());
    }
}
