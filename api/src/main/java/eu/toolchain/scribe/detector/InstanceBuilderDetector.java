package eu.toolchain.scribe.detector;

import eu.toolchain.scribe.EntityResolver;
import eu.toolchain.scribe.InstanceBuilder;
import eu.toolchain.scribe.reflection.JavaType;

import java.util.stream.Stream;

@FunctionalInterface
public interface InstanceBuilderDetector {
  Stream<Match<InstanceBuilder>> detect(EntityResolver resolver, JavaType type);
}
