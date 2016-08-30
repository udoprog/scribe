package eu.toolchain.scribe;

import lombok.Data;

import java.util.function.Consumer;

@Data
public class DefaultEntityFieldStreamEncoder<Target>
    implements EntityFieldStreamEncoder<Target, Object> {
  private final StreamEncoder<Target, Object> parent;
  private final String name;
  private final FieldReader reader;

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
