package eu.toolchain.scribe.detector;

import eu.toolchain.scribe.EntityResolver;
import eu.toolchain.scribe.reflection.JavaType;

import java.util.stream.Stream;

@FunctionalInterface
public interface TypeNameDetector {
  Stream<Match<String>> detect(EntityResolver resolver, JavaType type);
}
