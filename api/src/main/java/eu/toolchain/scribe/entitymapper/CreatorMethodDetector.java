package eu.toolchain.scribe.entitymapper;

import eu.toolchain.scribe.EntityResolver;
import eu.toolchain.scribe.JavaType;
import eu.toolchain.scribe.Match;
import eu.toolchain.scribe.creatormethod.CreatorMethod;

import java.util.stream.Stream;

@FunctionalInterface
public interface CreatorMethodDetector {
  Stream<Match<CreatorMethod>> detect(EntityResolver resolver, JavaType type);
}
