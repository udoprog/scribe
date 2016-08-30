package eu.toolchain.scribe.detector;

import eu.toolchain.scribe.EntityResolver;
import eu.toolchain.scribe.reflection.Annotations;
import eu.toolchain.scribe.reflection.JavaType;

import java.util.stream.Stream;

@FunctionalInterface
public interface FieldNameDetector {
  Stream<Match<String>> detect(EntityResolver resolver, JavaType type, Annotations field);
}
