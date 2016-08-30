package eu.toolchain.scribe.detector;

import eu.toolchain.scribe.DecodeValue;
import eu.toolchain.scribe.EntityResolver;
import eu.toolchain.scribe.reflection.JavaType;

import java.util.stream.Stream;

@FunctionalInterface
public interface DecodeValueDetector {
  Stream<Match<DecodeValue>> detect(EntityResolver resolver, JavaType type, JavaType fieldType);
}
