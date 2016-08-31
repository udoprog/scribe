package eu.toolchain.scribe;

import eu.toolchain.scribe.reflection.JavaType;
import lombok.Data;

@Data
class EntityEncodeValueStreamEncoder<Target, Source> implements StreamEncoder<Target, Source> {
  private final JavaType.Method valueMethod;
  private final StreamEncoder<Target, Source> parent;

  @Override
  public void streamEncode(
      final Context path, final Source instance, final Target target
  ) {
    final Source value;

    try {
      value = (Source) valueMethod.invoke(instance);
    } catch (Exception e) {
      throw path.error("failed to get value", e);
    }

    parent.streamEncode(path, value, target);
  }

  @Override
  public void streamEncodeEmpty(final Context path, final Target target) {
    parent.streamEncodeEmpty(path, target);
  }
}
