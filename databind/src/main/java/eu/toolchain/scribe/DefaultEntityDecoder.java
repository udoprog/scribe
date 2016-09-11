package eu.toolchain.scribe;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class DefaultEntityDecoder<Target, EntityTarget, Source>
    implements EntityDecoder<Target, EntityTarget, Source> {
  private final List<EntityFieldDecoder<Target, Object>> fields;
  private final ClassInstanceBuilder<Source> classInstanceBuilder;
  private final DecoderFactory<Target, EntityTarget> factory;

  @Override
  public Decoded<Source> decode(final Context path, final Target instance) {
    return factory.valueAsEntity(instance).map(i -> decodeEntity(path, i));
  }

  @Override
  public Source decodeEntity(
      final Context path, final EntityTarget entity
  ) {
    final EntityFieldsDecoder<Target> decoder = factory.newEntityDecoder(entity);
    return decodeEntity(path, entity, decoder);
  }

  @Override
  public Source decodeEntity(
      final Context path, final EntityTarget entity, final EntityFieldsDecoder<Target> decoder
  ) {
    final List<Object> arguments = new ArrayList<>();

    for (final EntityFieldDecoder<Target, Object> m : fields) {
      final Context p = path.push(m.getName());
      final Object value =
          decoder.decodeField(m, p).orElseThrow(() -> p.error("missing required field"));
      arguments.add(value);
    }

    return classInstanceBuilder.getInstanceBuilder().newInstance(path, arguments);
  }
}
