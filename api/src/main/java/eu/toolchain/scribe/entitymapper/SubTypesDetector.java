package eu.toolchain.scribe.entitymapper;

import eu.toolchain.scribe.EntityResolver;
import eu.toolchain.scribe.JavaType;
import eu.toolchain.scribe.Match;

import java.util.List;
import java.util.stream.Stream;

public interface SubTypesDetector {
  Stream<Match<List<SubType>>> detect(EntityResolver resolver, JavaType type);
}
