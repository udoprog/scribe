package eu.toolchain.scribe;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class DefaultEntityDecoder<Target> implements EntityDecoder<Target, Object> {
  private final List<EntityFieldDecoder<Target, Object>> fields;
  private final InstanceBuilder instanceBuilder;
  private final DecoderFactory<Target> factory;

  @Override
  public Decoded<Object> decode(
      final EntityFieldsDecoder<Target> decoder, final Context path
  ) {
    final List<Object> arguments = new ArrayList<>();

    for (final EntityFieldDecoder<Target, Object> m : fields) {
      final Context p = path.push(m.getName());
      arguments.add(decoder.decodeField(m, p).orElseThrow(() -> p.error("missing required field")));
    }

    try {
      return Decoded.of(instanceBuilder.newInstance(arguments));
    } catch (final Exception e) {
      throw path.error("Could not build instance using " + instanceBuilder, e);
    }
  }

  @Override
  public Decoded<Object> decode(final Context path, final Target instance) {
    return factory.newEntityDecoder(instance).flatMap(d -> decode(d, path));
  }
}
