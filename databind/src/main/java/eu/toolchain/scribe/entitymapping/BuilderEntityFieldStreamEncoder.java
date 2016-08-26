package eu.toolchain.scribe.entitymapping;

import eu.toolchain.scribe.Context;
import eu.toolchain.scribe.JavaType;
import eu.toolchain.scribe.StreamEncoder;
import eu.toolchain.scribe.fieldreader.FieldReader;
import eu.toolchain.scribe.typemapping.TypeMapping;
import lombok.Data;

import java.util.function.Consumer;

@Data
class BuilderEntityFieldStreamEncoder<Target> implements EntityFieldStreamEncoder<Target, Object> {
  private final String name;
  private final FieldReader reader;
  private final TypeMapping mapping;
  private final JavaType.Method setter;
  private final StreamEncoder<Target, Object> parent;

  @Override
  public void streamEncode(final Context path, final Object instance, final Target target) {
    parent.streamEncode(path, instance, target);
  }

  @Override
  public void streamEncodeOptionally(
      final Context path, final Object instance, final Target target,
      final Consumer<Runnable> callback
  ) {
    parent.streamEncodeOptionally(path, instance, target, callback);
  }
}
