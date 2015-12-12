package eu.toolchain.ogt.entitymapper;

import java.util.Optional;

import eu.toolchain.ogt.EntityResolver;
import eu.toolchain.ogt.JavaType;
import eu.toolchain.ogt.subtype.EntitySubTypesProvider;

public interface SubTypesDetector {
    Optional<EntitySubTypesProvider> detect(final EntityResolver resolver, final JavaType type);
}
