package eu.toolchain.scribe;

import eu.toolchain.scribe.reflection.JavaType;
import lombok.Data;

import java.util.function.Consumer;

@Data
class BuilderEntityFieldEncoder<Target, Source> implements EntityFieldEncoder<Target, Source> {
  private final String name;
  private final FieldReader reader;
  private final Mapping<Source> mapping;
  private final JavaType.Method setter;
  private final Flags flags;
  private final Encoder<Target, Source> parent;

  @Override
  public Target encode(final Context path, final Source instance) {
    return parent.encode(path, instance);
  }

  @Override
  public void encodeOptionally(
      final Context path, final Source instance, final Consumer<Target> consumer
  ) {
    parent.encodeOptionally(path, instance, consumer);
  }
}
