package eu.toolchain.scribe.entitymapper;

import eu.toolchain.scribe.EntityResolver;
import eu.toolchain.scribe.JavaType;
import eu.toolchain.scribe.Match;
import eu.toolchain.scribe.typemapping.EncodeValue;

import java.util.stream.Stream;

public interface EncodeValueDetector {
  Stream<Match<EncodeValue>> detect(EntityResolver resolver, JavaType type);
}
