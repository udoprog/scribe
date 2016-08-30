package eu.toolchain.scribe.detector;

import eu.toolchain.scribe.FieldReader;
import eu.toolchain.scribe.reflection.JavaType;

import java.util.stream.Stream;

@FunctionalInterface
public interface FieldReaderDetector {
  Stream<Match<FieldReader>> detect(
      final JavaType type, final String fieldName, final JavaType fieldType
  );
}
