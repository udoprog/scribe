package eu.toolchain.scribe.detector;

import eu.toolchain.scribe.ClassEncoding;
import eu.toolchain.scribe.EntityResolver;
import eu.toolchain.scribe.reflection.JavaType;

import java.util.stream.Stream;

@FunctionalInterface
public interface ClassEncodingDetector {
  Stream<Match<ClassEncoding<Object>>> detect(EntityResolver resolver, JavaType type);
}
