package eu.toolchain.scribe.entitymapper;

import eu.toolchain.scribe.EntityResolver;
import eu.toolchain.scribe.JavaType;
import eu.toolchain.scribe.Match;
import eu.toolchain.scribe.typemapping.DecodeValue;

import java.util.stream.Stream;

public interface DecodeValueDetector {
  Stream<Match<DecodeValue>> detect(EntityResolver resolver, JavaType type, JavaType fieldType);
}
