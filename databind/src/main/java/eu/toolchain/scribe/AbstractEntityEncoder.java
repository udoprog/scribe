package eu.toolchain.scribe;

import eu.toolchain.scribe.reflection.JavaType;
import lombok.Data;

import java.util.Map;

@Data
public class AbstractEntityEncoder<Target> implements EntityEncoder<Target, Object> {
  private final Map<JavaType, TypeEntry<Target>> byType;
  private final EncoderFactory<Target> factory;
  private final EntityFieldEncoder<Target, String> typeEncoder;

  @Override
  public Target encode(
      final EntityFieldsEncoder<Target> encoder, final Context path, final Object instance,
      final Runnable callback
  ) {
    final TypeEntry<Target> sub = byType.get(JavaType.of(instance.getClass()));

    if (sub == null) {
      throw new RuntimeException("Could not resolve subtype for: " + instance);
    }

    return sub.getEncoder().encode(encoder, path, instance, () -> {
      callback.run();
      encoder.encodeField(typeEncoder, path.push(typeEncoder.getName()), sub.getType());
    });
  }

  @Override
  public Target encode(final Context path, final Object instance) {
    return encode(factory.newEntityEncoder(), path, instance, EntityEncoder.EMPTY_CALLBACK);
  }

  @Override
  public Target encodeEmpty(final Context path) {
    return factory.newEntityEncoder().buildEmpty(path);
  }

  @Data
  public static class TypeEntry<Target> {
    final String type;
    final EntityEncoder<Target, Object> encoder;
  }
}
