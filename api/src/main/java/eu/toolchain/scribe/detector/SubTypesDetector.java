package eu.toolchain.scribe.detector;

import eu.toolchain.scribe.EntityResolver;
import eu.toolchain.scribe.SubType;
import eu.toolchain.scribe.reflection.JavaType;

import java.util.List;
import java.util.stream.Stream;

@FunctionalInterface
public interface SubTypesDetector {
  Stream<Match<List<SubType>>> detect(EntityResolver resolver, JavaType type);
}
