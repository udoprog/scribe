package eu.toolchain.ogt.entitymapper;

import java.util.Optional;

import eu.toolchain.ogt.EntityResolver;
import eu.toolchain.ogt.JavaType;
import eu.toolchain.ogt.creatormethod.CreatorField;

public interface PropertyNameDetector {
    Optional<String> detect(final EntityResolver resolver, final JavaType type,
            final CreatorField field);
}
