package eu.toolchain.scribe;

import eu.toolchain.scribe.reflection.JavaType;
import lombok.Data;

@Data
public class ConstructorEntityDecodeValueDecoder<Target, Source>
    implements Decoder<Target, Source> {
  private final JavaType.Constructor constructor;
  private final Decoder<Target, Source> parent;

  @SuppressWarnings("unchecked")
  @Override
  public Decoded<Source> decode(final Context path, final Target instance) {
    return parent.decode(path, instance).map(value -> {
      try {
        return (Source) constructor.newInstance(value);
      } catch (final Exception e) {
        throw path.error("failed to get value", e);
      }
    });
  }
}
