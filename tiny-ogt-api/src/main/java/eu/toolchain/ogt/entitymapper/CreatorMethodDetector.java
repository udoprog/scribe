package eu.toolchain.ogt.entitymapper;

import eu.toolchain.ogt.EntityResolver;
import eu.toolchain.ogt.type.JavaType;
import eu.toolchain.ogt.Match;
import eu.toolchain.ogt.creatormethod.CreatorMethod;

import java.util.stream.Stream;

public interface CreatorMethodDetector {
    Stream<Match<CreatorMethod>> detect(EntityResolver resolver, JavaType type);
}
