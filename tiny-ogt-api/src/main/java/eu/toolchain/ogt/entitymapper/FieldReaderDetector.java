package eu.toolchain.ogt.entitymapper;

import eu.toolchain.ogt.type.JavaType;
import eu.toolchain.ogt.Match;
import eu.toolchain.ogt.fieldreader.FieldReader;

import java.util.Optional;
import java.util.stream.Stream;

public interface FieldReaderDetector {
    Stream<Match<FieldReader>> detect(
        final JavaType type, final String fieldName, final Optional<JavaType> returnType
    );
}
