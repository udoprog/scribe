package eu.toolchain.scribe.entitymapper;

import eu.toolchain.scribe.Annotations;
import eu.toolchain.scribe.EntityResolver;
import eu.toolchain.scribe.JavaType;
import eu.toolchain.scribe.Match;

import java.util.stream.Stream;

public interface FieldNameDetector {
  Stream<Match<String>> detect(EntityResolver resolver, JavaType type, Annotations field);
}
