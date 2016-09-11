package eu.toolchain.scribe;

import lombok.Data;

@Data
public class DefaultEntityFieldDecoder<Target, Source>
    implements EntityFieldDecoder<Target, Source> {
  private final String name;
  private final Flags flags;
  private final Decoder<Target, Source> parent;

  @Override
  public Decoded<Source> decode(final Context path, final Target instance) {
    return parent.decode(path, instance);
  }

  @Override
  public Decoded<Source> decodeOptionally(
      final Context path, final Decoded<Target> instance
  ) {
    return parent.decodeOptionally(path, instance);
  }
}
