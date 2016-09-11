package eu.toolchain.scribe;

import eu.toolchain.scribe.reflection.JavaType;
import lombok.Data;

import java.util.function.Consumer;

@Data
class BuilderEntityFieldStreamEncoder<Target, Source>
    implements EntityFieldStreamEncoder<Target, Source> {
  private final String name;
  private final FieldReader reader;
  private final Mapping<Source> mapping;
  private final JavaType.Method setter;
  private final StreamEncoder<Target, Source> parent;

  @Override
  public void streamEncode(final Context path, final Source instance, final Target target) {
    parent.streamEncode(path, instance, target);
  }

  @Override
  public void streamEncodeOptionally(
      final Context path, final Source instance, final Target target,
      final Consumer<Runnable> callback
  ) {
    parent.streamEncodeOptionally(path, instance, target, callback);
  }
}
