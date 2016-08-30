package eu.toolchain.scribe;

import eu.toolchain.scribe.reflection.JavaType;
import lombok.Data;

@Data
class BuilderEntityFieldDecoder<Target> implements EntityFieldDecoder<Target, Object> {
  private final String name;
  private final FieldReader reader;
  private final Mapping mapping;
  private final JavaType.Method setter;
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

  public JavaType.Method setter() {
    return setter;
  }
}
