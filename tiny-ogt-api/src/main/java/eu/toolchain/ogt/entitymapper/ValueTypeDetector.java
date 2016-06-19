package eu.toolchain.ogt.entitymapper;

import eu.toolchain.ogt.EntityResolver;
import eu.toolchain.ogt.type.JavaType;
import eu.toolchain.ogt.Match;
import eu.toolchain.ogt.typemapping.TypeMapping;

import java.util.stream.Stream;

public interface ValueTypeDetector {
    Stream<Match<TypeMapping>> detect(EntityResolver resolver, JavaType type);
}
