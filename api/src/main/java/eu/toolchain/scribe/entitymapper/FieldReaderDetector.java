package eu.toolchain.scribe.entitymapper;

import eu.toolchain.scribe.JavaType;
import eu.toolchain.scribe.Match;
import eu.toolchain.scribe.fieldreader.FieldReader;

import java.util.stream.Stream;

public interface FieldReaderDetector {
  Stream<Match<FieldReader>> detect(
      final JavaType type, final String fieldName, final JavaType fieldType
  );
}
