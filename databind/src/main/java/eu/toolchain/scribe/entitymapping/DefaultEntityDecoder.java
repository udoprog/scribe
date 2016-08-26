package eu.toolchain.scribe.entitymapping;

import eu.toolchain.scribe.Context;
import eu.toolchain.scribe.Decoded;
import eu.toolchain.scribe.DecoderFactory;
import eu.toolchain.scribe.EntityDecoder;
import eu.toolchain.scribe.EntityFieldsDecoder;
import eu.toolchain.scribe.creatormethod.InstanceBuilder;
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
      arguments.add(decoder
          .decodeField(m, path.push(m.getName()))
          .orElseThrow(() -> path.error("missing required field")));
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
