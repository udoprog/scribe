package eu.toolchain.scribe;

import eu.toolchain.scribe.reflection.JavaType;
import lombok.Data;

import java.util.Map;

@Data
public class AbstractEntityStreamEncoder<Target> implements EntityStreamEncoder<Target, Object> {
  private final Map<JavaType, EntityEncoderEntry<Target>> byType;
  private final StreamEncoderFactory<Target> factory;
  private final EntityFieldStreamEncoder<Target, String> typeEncoder;

  @Override
  public void streamEncode(
      final EntityFieldsStreamEncoder<Target> encoder, final Context path, final Object instance,
      final Target target, final Runnable callback
  ) {
    final EntityEncoderEntry<Target> sub = byType.get(JavaType.of(instance.getClass()));

    if (sub == null) {
      throw path.error("Could not resolve subtype for instance (" + instance + ")");
    }

    sub.getEncoder().streamEncode(encoder, path, instance, target, () -> {
      callback.run();
      encoder.encodeField(typeEncoder, path.push(typeEncoder.getName()), sub.getType(), target);
    });
  }

  @Override
  public void streamEncode(final Context path, final Object instance, final Target target) {
    streamEncode(factory.newEntityStreamEncoder(), path, instance, target,
        EntityStreamEncoder.EMPTY_CALLBACK);
  }

  @Override
  public void streamEncodeEmpty(final Context path, final Target target) {
    factory.newEntityStreamEncoder().encodeEmpty(path, target);
  }

  @Data
  public static class EntityEncoderEntry<Target> {
    final String type;
    final EntityStreamEncoder<Target, Object> encoder;
  }
}
