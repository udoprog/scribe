package eu.toolchain.ogt.entitymapper;

import eu.toolchain.ogt.EntityResolver;
import eu.toolchain.ogt.JavaType;
import eu.toolchain.ogt.creatormethod.CreatorMethod;

import java.util.Optional;

public interface CreatorMethodDetector {
    Optional<CreatorMethod> detect(final EntityResolver resolver, final JavaType type);
}
