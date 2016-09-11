package eu.toolchain.scribe.detector;

import eu.toolchain.scribe.EntityField;
import eu.toolchain.scribe.EntityResolver;
import eu.toolchain.scribe.reflection.JavaType;

import java.util.List;
import java.util.stream.Stream;

@FunctionalInterface
public interface FieldsDetector {
  Stream<Match<List<EntityField>>> detect(final EntityResolver resolver, final JavaType type);
}
