package eu.toolchain.scribe.entitymapper;

import eu.toolchain.scribe.Annotations;
import eu.toolchain.scribe.EntityResolver;
import eu.toolchain.scribe.Flag;
import eu.toolchain.scribe.JavaType;

import java.util.stream.Stream;

@FunctionalInterface
public interface FieldFlagDetector {
  Stream<Flag> detect(EntityResolver resolver, JavaType type, Annotations field);
}
