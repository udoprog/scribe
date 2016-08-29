package eu.toolchain.scribe.datastore;

import com.google.datastore.v1.Key;
import com.google.datastore.v1.Value;
import eu.toolchain.scribe.EncodedTypeMapping;
import eu.toolchain.scribe.EntityMapperBuilder;
import eu.toolchain.scribe.Module;

import java.util.stream.Stream;

import static eu.toolchain.scribe.TypeMatcher.type;
import static eu.toolchain.scribe.typemapper.TypeMapper.matchMapper;

public class DatastoreModule implements Module {
  @Override
  public <T> EntityMapperBuilder<T> register(
      final EntityMapperBuilder<T> builder
  ) {
    builder.typeMapper(matchMapper(type(Value.class), EncodedTypeMapping::new));
    builder.typeMapper(matchMapper(type(Key.class), EncodedTypeMapping::new));

    builder.fieldFlagDetector((resolver, java, annotations) -> {
      if (annotations.isAnnotationPresent(EntityKey.class)) {
        return Stream.of(DatastoreFlags.KEY_FIELD);
      }

      return Stream.of();
    });

    builder.fieldFlagDetector((resolver, java, annotations) -> {
      return annotations.getAnnotation(ExcludeFromIndexes.class).map(a -> {
        return new DatastoreFlags.ExcludeFromIndexes(a.decode());
      });
    });

    return builder;
  }
}
