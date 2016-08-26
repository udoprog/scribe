package eu.toolchain.scribe.entitymapping;

import eu.toolchain.scribe.Context;
import eu.toolchain.scribe.Decoded;
import eu.toolchain.scribe.Decoder;
import lombok.Data;

@Data
public class DefaultEntityFieldDecoder<Target> implements EntityFieldDecoder<Target, Object> {
  private final Decoder<Target, Object> parent;
  private final String name;

  @Override
  public Decoded<Object> decode(final Context path, final Target instance) {
    return parent.decode(path, instance);
  }

  @Override
  public Decoded<Object> decodeOptionally(
      final Context path, final Decoded<Target> instance
  ) {
    return parent.decodeOptionally(path, instance);
  }
}
