package eu.toolchain.ogt.entitymapper;

import java.util.Optional;

import eu.toolchain.ogt.EntityResolver;
import eu.toolchain.ogt.JavaType;
import eu.toolchain.ogt.creatormethod.CreatorMethod;

public interface CreatorMethodDetector {
    Optional<CreatorMethod> detect(final EntityResolver resolver, final JavaType type);
}
