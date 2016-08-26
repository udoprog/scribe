package eu.toolchain.scribe.typemapping;

import eu.toolchain.scribe.Context;
import eu.toolchain.scribe.StreamEncoder;
import eu.toolchain.scribe.entitymapping.EntityFieldStreamEncoder;
import lombok.Data;

import java.util.function.Consumer;

@Data
public class TypeEntityFieldStreamEncoder<Target>
    implements EntityFieldStreamEncoder<Target, String> {
  private final String name;
  private final StreamEncoder<Target, String> encoder;

  @Override
  public String getName() {
    return name;
  }

  @Override
  public void streamEncode(final Context path, final String instance, final Target target) {
    encoder.streamEncode(path, instance, target);
  }

  @Override
  public void streamEncodeOptionally(
      final Context path, final String instance, final Target target,
      final Consumer<Runnable> callback
  ) {
    encoder.streamEncodeOptionally(path, instance, target, callback);
  }
}
