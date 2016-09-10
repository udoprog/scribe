package eu.toolchain.scribe;

import eu.toolchain.scribe.reflection.JavaType;
import lombok.Data;

import java.util.List;

@Data
public class BuilderEntityDecoder<Target, EntityTarget>
    implements EntityDecoder<Target, EntityTarget, Object> {
  private final List<BuilderEntityFieldDecoder<Target>> fields;
  private final JavaType.Method newInstance;
  private final JavaType.Method build;
  private final DecoderFactory<Target, EntityTarget> factory;

  @Override
  public Object decode(
      final EntityFieldsDecoder<Target> encoder, final Context path
  ) {
    final Object builder;

    try {
      builder = newInstance.invoke(null);
    } catch (final ReflectiveOperationException e) {
      throw new RuntimeException(
          "Failed to create instance forAnnotation builder (" + newInstance + ")", e);
    }

    for (final BuilderEntityFieldDecoder<Target> m : fields) {
      final Context p = path.push(m.getName());

      final Object value =
          encoder.decodeField(m, p).orElseThrow(() -> p.error("missing required field"));

      try {
        m.setter().invoke(builder, value);
      } catch (final Exception e) {
        throw p.error(
            "Failed to invoke builder method " + m.getName() + " with argument (" + value + ")", e);
      }
    }

    try {
      return build.invoke(builder);
    } catch (final Exception e) {
      throw new RuntimeException("Could not build instance using " + build, e);
    }
  }

  @Override
  public Decoded<Object> decode(final Context path, final Target instance) {
    return factory.valueAsEntity(instance).map(i -> decodeEntity(path, i));
  }

  @Override
  public Object decodeEntity(final Context path, final EntityTarget entity) {
    return decode(factory.newEntityDecoder(entity), path);
  }
}
