package eu.toolchain.scribe;

import eu.toolchain.scribe.reflection.JavaType;
import lombok.Data;

import java.util.Map;

@Data
public class AbstractEntityEncoder<Target, EntityTarget, Source>
    implements EntityEncoder<Target, EntityTarget, Source> {
  private final Map<JavaType, TypeEntry<Target, EntityTarget, Source>> byType;
  private final EncoderFactory<Target, EntityTarget> factory;
  private final EntityFieldEncoder<Target, String> typeEncoder;

  @Override
  public EntityTarget encodeEntity(
      final EntityFieldsEncoder<Target, EntityTarget> encoder, final Context path,
      final Source instance, final Runnable callback
  ) {
    final TypeEntry<Target, EntityTarget, Source> sub =
        byType.get(JavaType.of(instance.getClass()));

    if (sub == null) {
      throw path.error("Could not resolve subtype for (" + instance + ")");
    }

    return sub.getEncoder().encodeEntity(encoder, path, instance, () -> {
      callback.run();
      encoder.encodeField(typeEncoder, path.push(typeEncoder.getName()), sub.getType());
    });
  }

  @Override
  public EntityTarget encodeEntity(final Context path, final Source instance) {
    return encodeEntity(factory.newEntityEncoder(), path, instance, EntityEncoder.EMPTY_CALLBACK);
  }

  @Override
  public Target encode(final Context path, final Source instance) {
    return factory.entityAsValue(encodeEntity(path, instance));
  }

  @Override
  public Target encodeEmpty(final Context path) {
    return factory.entityAsValue(factory.newEntityEncoder().buildEmpty(path));
  }

  @Data
  public static class TypeEntry<Target, EntityTarget, Source> {
    final String type;
    final EntityEncoder<Target, EntityTarget, Source> encoder;
  }
}
