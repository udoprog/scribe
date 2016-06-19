package eu.toolchain.ogt.entitymapper;

import eu.toolchain.ogt.Match;
import eu.toolchain.ogt.fieldreader.FieldReader;

import java.lang.reflect.Type;
import java.util.Optional;
import java.util.stream.Stream;

public interface FieldReaderDetector {
    Stream<Match<FieldReader>> detect(
        final Type type, final String fieldName, final Optional<Type> returnType
    );
}
