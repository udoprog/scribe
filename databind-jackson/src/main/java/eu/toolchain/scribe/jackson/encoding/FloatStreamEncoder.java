package eu.toolchain.scribe.jackson.encoding;

import com.fasterxml.jackson.core.JsonGenerator;
import eu.toolchain.scribe.Context;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class FloatStreamEncoder extends AbstractStreamEncoder<Float> {
  @Override
  public void streamEncode(
      final Context path, final Float instance, final JsonGenerator target
  ) {
    try {
      target.writeNumber(instance);
    } catch (final Exception e) {
      throw path.error(e);
    }
  }

  private static final FloatStreamEncoder INSTANCE = new FloatStreamEncoder();

  public static FloatStreamEncoder get() {
    return INSTANCE;
  }
}
