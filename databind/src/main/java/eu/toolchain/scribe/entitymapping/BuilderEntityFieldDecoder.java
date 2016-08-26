package eu.toolchain.scribe.entitymapping;

import eu.toolchain.scribe.Context;
import eu.toolchain.scribe.Decoded;
import eu.toolchain.scribe.Decoder;
import eu.toolchain.scribe.JavaType;
import eu.toolchain.scribe.fieldreader.FieldReader;
import eu.toolchain.scribe.typemapping.TypeMapping;
import lombok.Data;

@Data
class BuilderEntityFieldDecoder<Target> implements EntityFieldDecoder<Target, Object> {
  private final String name;
  private final FieldReader reader;
  private final TypeMapping mapping;
  private final JavaType.Method setter;
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

  public JavaType.Method setter() {
    return setter;
  }
}
