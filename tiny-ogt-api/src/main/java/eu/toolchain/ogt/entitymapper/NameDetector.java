package eu.toolchain.ogt.entitymapper;

import java.util.Optional;

import eu.toolchain.ogt.EntityResolver;
import eu.toolchain.ogt.JavaType;

public interface NameDetector {
    Optional<String> detect(final EntityResolver resolver, final JavaType type);
}
