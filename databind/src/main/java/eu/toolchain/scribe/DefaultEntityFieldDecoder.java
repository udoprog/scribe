package eu.toolchain.scribe;

import lombok.Data;

@Data
public class DefaultEntityFieldDecoder<Target> implements EntityFieldDecoder<Target, Object> {
  private final String name;
  private final Flags flags;
  private final Decoder<Target, Object> parent;

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
