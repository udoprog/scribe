package eu.toolchain.scribe.detector;

import eu.toolchain.scribe.EncodeValue;
import eu.toolchain.scribe.EntityResolver;
import eu.toolchain.scribe.reflection.JavaType;

import java.util.stream.Stream;

@FunctionalInterface
public interface EncodeValueDetector {
  Stream<Match<EncodeValue<Object>>> detect(EntityResolver resolver, JavaType type);
}
