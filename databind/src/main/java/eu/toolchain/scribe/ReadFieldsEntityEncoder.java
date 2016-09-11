package eu.toolchain.scribe;

import lombok.Data;

import java.util.List;

@Data
public class ReadFieldsEntityEncoder<Target, EntityTarget, Source>
    implements EntityEncoder<Target, EntityTarget, Source> {
  private final List<? extends Field<Target, Object>> fields;
  private final EncoderFactory<Target, EntityTarget> factory;

  @Override
  public EntityTarget encodeEntity(
      final EntityFieldsEncoder<Target, EntityTarget> encoder, final Context path,
      final Source instance, final Runnable callback
  ) {
    callback.run();

    for (final Field<Target, Object> m : fields) {
      final EntityFieldEncoder<Target, Object> fieldEncoder = m.getEncoder();
      final FieldReader reader = m.getReader();

      final Context p = path.push(fieldEncoder.getName());

      final Object value = reader.read(p, instance);

      if (value == null) {
        throw p.error("Null value read from " + reader);
      }

      encoder.encodeField(fieldEncoder, p, value);
    }

    return encoder.build();
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
  public static class Field<Target, Source> {
    private final EntityFieldEncoder<Target, Source> encoder;
    private final FieldReader reader;
  }
}
