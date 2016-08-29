package eu.toolchain.scribe.typemapping;

import eu.toolchain.scribe.Context;
import eu.toolchain.scribe.Encoder;
import eu.toolchain.scribe.Flags;
import eu.toolchain.scribe.entitymapping.EntityFieldEncoder;
import lombok.Data;

import java.util.function.Consumer;

@Data
public class TypeEntityFieldEncoder<Target> implements EntityFieldEncoder<Target, String> {
  private final String name;
  private final Encoder<Target, String> encoder;

  @Override
  public Flags getFlags() {
    return Flags.empty();
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public Target encode(final Context path, final String instance) {
    return encoder.encode(path, instance);
  }

  @Override
  public void encodeOptionally(
      final Context path, final String instance, final Consumer<Target> consumer
  ) {
    encoder.encodeOptionally(path, instance, consumer);
  }
}
