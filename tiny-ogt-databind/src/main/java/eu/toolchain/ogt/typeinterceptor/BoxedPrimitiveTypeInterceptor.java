package eu.toolchain.ogt.typeinterceptor;

import eu.toolchain.ogt.Annotations;
import eu.toolchain.ogt.BoxedPrimitiveType;
import eu.toolchain.ogt.EntityResolver;
import eu.toolchain.ogt.JavaType;
import eu.toolchain.ogt.type.BoxedPrimitiveTypeMapping;
import eu.toolchain.ogt.type.TypeMapping;

import java.util.Optional;

public class BoxedPrimitiveTypeInterceptor {
    public static Optional<TypeMapping> intercept(
        final EntityResolver resolver, final JavaType type, final Annotations annotations
    ) {
        return BoxedPrimitiveType.detect(type).map(p -> new BoxedPrimitiveTypeMapping(type, p));
    }
}
