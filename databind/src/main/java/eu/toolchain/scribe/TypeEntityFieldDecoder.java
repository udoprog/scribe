package eu.toolchain.scribe;

import lombok.Data;

@Data
public class TypeEntityFieldDecoder<Target> implements EntityFieldDecoder<Target, String> {
  private final String name;
  private final Decoder<Target, String> decoder;

  @Override
  public Flags getFlags() {
    return Flags.empty();
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public Decoded<String> decode(final Context path, final Target instance) {
    return decoder.decode(path, instance);
  }

  @Override
  public Decoded<String> decodeOptionally(final Context path, final Decoded<Target> instance) {
    return decoder.decodeOptionally(path, instance);
  }
}
