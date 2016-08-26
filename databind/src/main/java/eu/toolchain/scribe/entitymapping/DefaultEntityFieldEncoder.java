package eu.toolchain.scribe.entitymapping;

import eu.toolchain.scribe.Context;
import eu.toolchain.scribe.Encoder;
import eu.toolchain.scribe.fieldreader.FieldReader;
import lombok.Data;

@Data
public class DefaultEntityFieldEncoder<Target> implements EntityFieldEncoder<Target, Object> {
  private final Encoder<Target, Object> parent;
  private final String name;
  private final FieldReader reader;

  @Override
  public Target encode(final Context path, final Object instance) {
    return parent.encode(path, instance);
  }
}
