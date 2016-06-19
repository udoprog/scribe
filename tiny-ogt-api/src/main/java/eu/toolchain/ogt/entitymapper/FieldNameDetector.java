package eu.toolchain.ogt.entitymapper;

import eu.toolchain.ogt.Annotations;
import eu.toolchain.ogt.EntityResolver;
import eu.toolchain.ogt.Match;

import java.lang.reflect.Type;
import java.util.stream.Stream;

public interface FieldNameDetector {
    Stream<Match<String>> detect(EntityResolver resolver, Type type, Annotations field);
}
