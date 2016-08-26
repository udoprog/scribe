package eu.toolchain.scribe.entitymapping;

import eu.toolchain.scribe.Context;
import eu.toolchain.scribe.EncoderFactory;
import eu.toolchain.scribe.EntityEncoder;
import eu.toolchain.scribe.EntityFieldsEncoder;
import eu.toolchain.scribe.fieldreader.FieldReader;
import lombok.Data;

import java.util.List;

@Data
public class ReadFieldsEntityEncoder<Target> implements EntityEncoder<Target, Object> {
  private final List<? extends ReadFieldsEntityField<Target, Object>> fields;
  private final EncoderFactory<Target> factory;

  @Override
  public Target encode(
      final EntityFieldsEncoder<Target> encoder, final Context path, final Object instance,
      final Runnable callback
  ) {
    callback.run();

    for (final ReadFieldsEntityField<Target, Object> m : fields) {
      final EntityFieldEncoder<Target, Object> fieldEncoder = m.getEncoder();
      final FieldReader reader = m.getReader();

      final Context p = path.push(fieldEncoder.getName());

      final Object value;

      try {
        value = reader.read(instance);
      } catch (final Exception e) {
        throw p.error("Failed to read value using " + reader, e);
      }

      if (value == null) {
        throw p.error("Null value read from " + reader);
      }

      try {
        encoder.encodeField(fieldEncoder, p, value);
      } catch (Exception e) {
        throw p.error("Failed to encode field", e);
      }
    }

    return encoder.build();
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
  public static class ReadFieldsEntityField<Target, Source> {
    private final EntityFieldEncoder<Target, Source> encoder;
    private final FieldReader reader;
  }
}
