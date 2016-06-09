package eu.toolchain.ogt.typemappinginterceptor;

import eu.toolchain.ogt.Annotations;
import eu.toolchain.ogt.EntityResolver;
import eu.toolchain.ogt.JavaType;
import eu.toolchain.ogt.annotations.Bytes;
import eu.toolchain.ogt.type.EncodedBytesTypeMapping;
import eu.toolchain.ogt.type.TypeMapping;

import java.util.Optional;

public class BytesTypeMappingInterceptor {
    public static Optional<TypeMapping> intercept(
        final EntityResolver resolver, final JavaType type, final Annotations annotations
    ) {
        if (!annotations.isAnnotationPresent(Bytes.class)) {
            return Optional.empty();
        }

        return Optional.of(new EncodedBytesTypeMapping(type));
    }
}
