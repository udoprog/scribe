package eu.toolchain.scribe.automatter;

import eu.toolchain.scribe.BuilderClassEncoding;
import eu.toolchain.scribe.EntityField;
import eu.toolchain.scribe.EntityResolver;
import eu.toolchain.scribe.GetterFieldReader;
import eu.toolchain.scribe.Module;
import eu.toolchain.scribe.ScribeBuilder;
import eu.toolchain.scribe.detector.Match;
import eu.toolchain.scribe.detector.MatchPriority;
import eu.toolchain.scribe.reflection.Annotations;
import eu.toolchain.scribe.reflection.JavaType;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

public class AutoMatterModule implements Module {
  @Override
  public void register(final ScribeBuilder b) {
    b.classEncoding(BuilderClassEncoding.forRelatedClass(
        type -> JavaType.ofClassName(type.getTypeName() + "Builder")));

    b.fields(AutoMatterFields::detect);

    b.fieldReader(GetterFieldReader.forName((type, name) -> name));
  }

  static class AutoMatterFields {
    static Stream<Match<List<EntityField>>> detect(
        final EntityResolver resolver, final JavaType type
    ) {
      if (!type.isAbstract()) {
        return Stream.of();
      }

      final Iterator<JavaType.Method> it = type.getMethods().iterator();

      int index = 0;

      final List<EntityField> fields = new ArrayList<>();

      while (it.hasNext()) {
        final JavaType.Method m = it.next();
        final Annotations annotations = Annotations.of(m.getAnnotationStream());
        final String name =
            resolver.detectFieldName(m.getReturnType(), annotations, index++).orElseGet(m::getName);
        fields.add(new EntityField(m.getReturnType(), annotations, name, m.getName()));
      }

      return Stream.of(fields).map(Match.withPriority(MatchPriority.HIGH));
    }
  }
}
