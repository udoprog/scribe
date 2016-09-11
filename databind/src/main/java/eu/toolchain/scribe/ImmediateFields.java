package eu.toolchain.scribe;

import eu.toolchain.scribe.detector.Match;
import eu.toolchain.scribe.detector.MatchPriority;
import eu.toolchain.scribe.reflection.Annotations;
import eu.toolchain.scribe.reflection.JavaType;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class ImmediateFields {
  public static Stream<Match<List<EntityField>>> detect(
      final EntityResolver resolver, final JavaType type
  ) {
    final Iterator<JavaType.Field> it = type.getFields().filter(f -> !f.isStatic()).iterator();

    int index = 0;

    final List<EntityField> fields = new ArrayList<>();

    while (it.hasNext()) {
      final JavaType.Field f = it.next();

      fields.add(
          new EntityField(true, index++, Annotations.of(f.getAnnotationStream()), f.getFieldType(),
              Optional.of(f.getName())));
    }

    return Stream.of(fields).map(Match.withPriority(MatchPriority.DEFAULT));
  }
}
