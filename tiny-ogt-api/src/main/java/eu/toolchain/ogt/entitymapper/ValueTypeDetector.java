package eu.toolchain.ogt.entitymapper;

import eu.toolchain.ogt.EntityResolver;
import eu.toolchain.ogt.JavaType;
import eu.toolchain.ogt.type.TypeMapping;

import java.util.Optional;

public interface ValueTypeDetector {
    Optional<TypeMapping> detect(final EntityResolver resolver, final JavaType type);
}
