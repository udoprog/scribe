package eu.toolchain.scribe;

import eu.toolchain.scribe.reflection.JavaType;
import lombok.Data;

@Data
class EntityEncodeValueEncoder<Target, Source> implements Encoder<Target, Source> {
  private final JavaType.Method valueMethod;
  private final Encoder<Target, Source> parent;

  @SuppressWarnings("unchecked")
  @Override
  public Target encode(final Context path, final Source instance) {
    final Source value;

    try {
      value = (Source) valueMethod.invoke(instance);
    } catch (Exception e) {
      throw path.error("failed to get value", e);
    }

    return parent.encode(path, value);
  }

  @Override
  public Target encodeEmpty(final Context path) {
    return parent.encodeEmpty(path);
  }
}
