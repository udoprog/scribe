package eu.toolchain.scribe;

import eu.toolchain.scribe.reflection.JavaType;
import lombok.Data;

@Data
public class StaticMethodEntityDecodeValueDecoder<Target, Source>
    implements Decoder<Target, Source> {
  private final Decoder<Target, Source> parent;
  private final JavaType.Method method;

  @SuppressWarnings("unchecked")
  @Override
  public Decoded<Source> decode(final Context path, final Target instance) {
    return parent.decode(path, instance).map(value -> {
      try {
        return (Source) method.invoke(null, value);
      } catch (Exception e) {
        throw path.error("failed to get value", e);
      }
    });
  }
}
