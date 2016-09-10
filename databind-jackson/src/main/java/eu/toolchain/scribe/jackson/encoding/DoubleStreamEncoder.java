package eu.toolchain.scribe.jackson.encoding;

import com.fasterxml.jackson.core.JsonGenerator;
import eu.toolchain.scribe.Context;
import lombok.Data;

@Data
public class DoubleStreamEncoder extends AbstractStreamEncoder<Double> {
  @Override
  public void streamEncode(
      final Context path, final Double instance, final JsonGenerator target
  ) {
    try {
      target.writeNumber(instance);
    } catch (final Exception e) {
      throw path.error(e);
    }
  }

  private static final DoubleStreamEncoder INSTANCE = new DoubleStreamEncoder();

  public static DoubleStreamEncoder get() {
    return INSTANCE;
  }
}
