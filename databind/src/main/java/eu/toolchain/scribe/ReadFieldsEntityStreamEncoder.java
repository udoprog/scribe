package eu.toolchain.scribe;

import lombok.Data;

import java.util.List;

@Data
public class ReadFieldsEntityStreamEncoder<Target, Source>
    implements EntityStreamEncoder<Target, Source> {
  private final List<ReadFieldsEntityField<Target, Object>> fields;
  private final StreamEncoderFactory<Target> factory;

  @Override
  public void streamEncode(
      final EntityFieldsStreamEncoder<Target> encoder, final Context path, final Source instance,
      final Target target, final Runnable callback
  ) {
    encoder.encodeStart(path, target);

    callback.run();

    for (final ReadFieldsEntityField<Target, Object> m : fields) {
      final EntityFieldStreamEncoder<Target, Object> fieldEncoder = m.getEncoder();
      final FieldReader reader = m.getReader();

      final Context p = path.push(fieldEncoder.getName());

      final Object value = reader.read(p, instance);

      if (value == null) {
        throw p.error("null value read");
      }

      encoder.encodeField(fieldEncoder, p, value, target);
    }

    encoder.encodeEnd(path, target);
  }

  @Override
  public void streamEncode(final Context path, final Source instance, final Target target) {
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
