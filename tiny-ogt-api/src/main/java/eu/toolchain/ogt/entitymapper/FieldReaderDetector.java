package eu.toolchain.ogt.entitymapper;

import java.util.Optional;

import eu.toolchain.ogt.JavaType;
import eu.toolchain.ogt.fieldreader.FieldReader;

public interface FieldReaderDetector {
    Optional<FieldReader> detect(final JavaType type, final JavaType returnType,
            final String fieldName);
}
