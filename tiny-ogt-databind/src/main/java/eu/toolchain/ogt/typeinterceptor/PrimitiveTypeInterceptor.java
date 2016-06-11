package eu.toolchain.ogt.typeinterceptor;

import eu.toolchain.ogt.Annotations;
import eu.toolchain.ogt.EntityResolver;
import eu.toolchain.ogt.JavaType;
import eu.toolchain.ogt.PrimitiveType;
import eu.toolchain.ogt.type.PrimitiveTypeMapping;
import eu.toolchain.ogt.type.TypeMapping;

import java.util.Optional;

public class PrimitiveTypeInterceptor {
    public static Optional<TypeMapping> intercept(
        final EntityResolver resolver, final JavaType type, final Annotations annotations
    ) {
        return PrimitiveType.detect(type).map(p -> new PrimitiveTypeMapping(type, p));
    }
}
