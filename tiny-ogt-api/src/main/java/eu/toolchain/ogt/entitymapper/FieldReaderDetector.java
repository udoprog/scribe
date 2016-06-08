package eu.toolchain.ogt.entitymapper;

import java.util.Optional;

import eu.toolchain.ogt.JavaType;
import eu.toolchain.ogt.fieldreader.FieldReader;

public interface FieldReaderDetector {
    Optional<FieldReader> detect(final JavaType type, final String fieldName,
            final Optional<JavaType> returnType);
}
