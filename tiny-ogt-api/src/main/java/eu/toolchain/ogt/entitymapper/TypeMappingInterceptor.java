package eu.toolchain.ogt.entitymapper;

import eu.toolchain.ogt.Annotations;
import eu.toolchain.ogt.EntityResolver;
import eu.toolchain.ogt.JavaType;
import eu.toolchain.ogt.type.TypeMapping;

import java.util.Optional;

public interface TypeMappingInterceptor {
    Optional<TypeMapping> intercept(
        EntityResolver resolver, JavaType type, Annotations annotations
    );
}
