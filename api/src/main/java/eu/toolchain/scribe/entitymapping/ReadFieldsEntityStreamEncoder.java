package eu.toolchain.scribe.entitymapping;

import eu.toolchain.scribe.Context;
import eu.toolchain.scribe.EntityFieldsStreamEncoder;
import eu.toolchain.scribe.EntityStreamEncoder;
import eu.toolchain.scribe.StreamEncoderFactory;
import eu.toolchain.scribe.fieldreader.FieldReader;
import lombok.Data;

import java.util.List;

@Data
public class ReadFieldsEntityStreamEncoder<Target> implements EntityStreamEncoder<Target, Object> {
  private final List<ReadFieldsEntityField<Target, Object>> fields;
  private final StreamEncoderFactory<Target> factory;

  @Override
  public void streamEncode(
      final EntityFieldsStreamEncoder<Target> encoder, final Context path, final Object instance,
      final Target target, final Runnable callback
  ) {
    encoder.encodeStart(target);

    callback.run();

    for (final ReadFieldsEntityField<Target, Object> m : fields) {
      final EntityFieldStreamEncoder<Target, Object> fieldEncoder = m.getEncoder();
      final FieldReader reader = m.getReader();

      final Context p = path.push(fieldEncoder.getName());

      final Object value;

      try {
        value = reader.read(instance);
      } catch (final Exception e) {
        throw p.error("failed to read value", e);
      }

      if (value == null) {
        throw p.error("null value read");
      }

      try {
        encoder.encodeField(fieldEncoder, p, value, target);
      } catch (Exception e) {
        throw p.error("failed to encode", e);
      }
    }

    encoder.encodeEnd(target);
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
  public static class ReadFieldsEntityField<Target, Source> {
    private final EntityFieldStreamEncoder<Target, Source> encoder;
    private final FieldReader reader;
  }
}
