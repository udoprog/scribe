package eu.toolchain.ogt.entitymapper;

import eu.toolchain.ogt.EntityResolver;
import eu.toolchain.ogt.JavaType;
import eu.toolchain.ogt.subtype.EntitySubTypesProvider;

import java.util.Optional;

public interface SubTypesDetector {
    Optional<EntitySubTypesProvider> detect(final EntityResolver resolver, final JavaType type);
}
