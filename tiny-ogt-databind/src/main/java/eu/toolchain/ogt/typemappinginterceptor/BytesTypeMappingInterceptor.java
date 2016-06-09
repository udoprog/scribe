package eu.toolchain.ogt.typemappinginterceptor;

import eu.toolchain.ogt.Annotations;
import eu.toolchain.ogt.EntityResolver;
import eu.toolchain.ogt.JavaType;
import eu.toolchain.ogt.annotations.Bytes;
import eu.toolchain.ogt.type.EncodedBytesTypeMapping;
import eu.toolchain.ogt.type.EncodedForeignBytesTypeMapping;
import eu.toolchain.ogt.type.TypeMapping;

import java.util.Optional;

public class BytesTypeMappingInterceptor {
    public static Optional<TypeMapping> intercept(
        final EntityResolver resolver, final JavaType type, final Annotations annotations
    ) {
        return annotations.getAnnotation(Bytes.class).map(b -> {
            if (b.foreign()) {
                return new EncodedForeignBytesTypeMapping(type);
            }

            return new EncodedBytesTypeMapping(resolver.mapping(type));
        });
    }
}
