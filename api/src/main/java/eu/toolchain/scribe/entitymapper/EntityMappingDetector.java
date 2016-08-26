package eu.toolchain.scribe.entitymapper;

import eu.toolchain.scribe.EntityResolver;
import eu.toolchain.scribe.JavaType;
import eu.toolchain.scribe.Match;
import eu.toolchain.scribe.entitymapping.EntityMapping;

import java.util.stream.Stream;

public interface EntityMappingDetector {
  Stream<Match<EntityMapping>> detect(EntityResolver resolver, JavaType type);
}
