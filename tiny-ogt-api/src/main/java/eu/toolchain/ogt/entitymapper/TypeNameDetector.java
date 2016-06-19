package eu.toolchain.ogt.entitymapper;

import eu.toolchain.ogt.EntityResolver;
import eu.toolchain.ogt.type.JavaType;
import eu.toolchain.ogt.Match;

import java.util.stream.Stream;

public interface TypeNameDetector {
    Stream<Match<String>> detect(EntityResolver resolver, JavaType type);
}
