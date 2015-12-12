package eu.toolchain.ogt.entitymapper;

import java.util.Optional;

import eu.toolchain.ogt.EntityResolver;
import eu.toolchain.ogt.JavaType;
import eu.toolchain.ogt.type.TypeMapping;

public interface ValueTypeDetector {
    Optional<TypeMapping> detect(final EntityResolver resolver, final JavaType type);
}
