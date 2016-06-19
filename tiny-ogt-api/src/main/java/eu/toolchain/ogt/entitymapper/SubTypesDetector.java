package eu.toolchain.ogt.entitymapper;

import eu.toolchain.ogt.EntityResolver;
import eu.toolchain.ogt.Match;
import eu.toolchain.ogt.typemapping.EntityTypeMapping;

import java.lang.reflect.Type;
import java.util.List;
import java.util.stream.Stream;

public interface SubTypesDetector {
    Stream<Match<List<EntityTypeMapping>>> detect(EntityResolver resolver, Type type);
}
