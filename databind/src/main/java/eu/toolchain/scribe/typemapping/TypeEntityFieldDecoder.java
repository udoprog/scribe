package eu.toolchain.scribe.typemapping;

import eu.toolchain.scribe.Context;
import eu.toolchain.scribe.Decoded;
import eu.toolchain.scribe.Decoder;
import eu.toolchain.scribe.entitymapping.EntityFieldDecoder;
import lombok.Data;

@Data
public class TypeEntityFieldDecoder<Target> implements EntityFieldDecoder<Target, String> {
  private final String name;
  private final Decoder<Target, String> decoder;

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
