package eu.toolchain.ogt.entitymapper;

import eu.toolchain.ogt.JavaType;
import eu.toolchain.ogt.fieldreader.FieldReader;

import java.util.Optional;

public interface FieldReaderDetector {
    Optional<FieldReader> detect(
        final JavaType type, final String fieldName, final Optional<JavaType> returnType
    );
}
