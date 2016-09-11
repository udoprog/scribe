package eu.toolchain.scribe;

import eu.toolchain.scribe.reflection.JavaType;
import lombok.Data;

import java.util.Collections;
import java.util.List;

@Data
public class BuilderEntityDecoder<Target, EntityTarget, Source>
    implements EntityDecoder<Target, EntityTarget, Source> {
  private final List<BuilderEntityFieldDecoder<Target, ?>> fields;
  private final InstanceBuilder<Object> newBuilder;
  private final JavaType.Method build;
  private final DecoderFactory<Target, EntityTarget> factory;

  @Override
  public Decoded<Source> decode(final Context path, final Target instance) {
    return factory.valueAsEntity(instance).map(i -> decodeEntity(path, i));
  }

  @Override
  public Source decodeEntity(final Context path, final EntityTarget entity) {
    final EntityFieldsDecoder<Target> decoder = factory.newEntityDecoder(entity);
    return decodeEntity(path, entity, decoder);
  }

  @SuppressWarnings("unchecked")
  @Override
  public Source decodeEntity(
      final Context path, final EntityTarget entity, final EntityFieldsDecoder<Target> decoder
  ) {
    final Object builder = newBuilder.newInstance(path, Collections.emptyList());

    for (final BuilderEntityFieldDecoder<Target, ?> m : fields) {
      final Context p = path.push(m.getName());

      final Object value =
          decoder.decodeField(m, p).orElseThrow(() -> p.error("missing required field"));

      try {
        m.getSetter().invoke(builder, value);
      } catch (final Exception e) {
        throw p.error(
            "Failed to invoke builder method " + m.getName() + " with argument (" + value + ")", e);
      }
    }

    try {
      return (Source) build.invoke(builder);
    } catch (final Exception e) {
      throw path.error("Could not build instance using " + build, e);
    }
  }
}
