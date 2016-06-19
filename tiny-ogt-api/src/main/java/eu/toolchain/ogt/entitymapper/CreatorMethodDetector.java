package eu.toolchain.ogt.entitymapper;

import eu.toolchain.ogt.EntityResolver;
import eu.toolchain.ogt.Match;
import eu.toolchain.ogt.creatormethod.CreatorMethod;

import java.lang.reflect.Type;
import java.util.stream.Stream;

public interface CreatorMethodDetector {
    Stream<Match<CreatorMethod>> detect(EntityResolver resolver, Type type);
}
