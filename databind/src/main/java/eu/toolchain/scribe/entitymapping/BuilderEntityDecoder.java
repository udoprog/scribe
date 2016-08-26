package eu.toolchain.scribe.entitymapping;

import eu.toolchain.scribe.Context;
import eu.toolchain.scribe.Decoded;
import eu.toolchain.scribe.DecoderFactory;
import eu.toolchain.scribe.EntityDecoder;
import eu.toolchain.scribe.EntityFieldsDecoder;
import eu.toolchain.scribe.JavaType;
import lombok.Data;

import java.util.List;

@Data
public class BuilderEntityDecoder<Target> implements EntityDecoder<Target, Object> {
  private final List<BuilderEntityFieldDecoder<Target>> fields;
  private final JavaType.Method newInstance;
  private final JavaType.Method build;
  private final DecoderFactory<Target> factory;

  @Override
  public Decoded<Object> decode(
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
        throw p.error("Failed to invoke builder method " + m.getName() + " with argument (" +
            value +
            ")", e);
      }
    }

    try {
      return Decoded.of(build.invoke(builder));
    } catch (final Exception e) {
      throw new RuntimeException("Could not build instance using " + build, e);
    }
  }

  @Override
  public Decoded<Object> decode(final Context path, final Target instance) {
    return factory.newEntityDecoder(instance).flatMap(d -> decode(d, path));
  }
}
