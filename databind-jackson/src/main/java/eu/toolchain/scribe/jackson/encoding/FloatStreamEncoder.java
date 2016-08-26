package eu.toolchain.scribe.jackson.encoding;

import com.fasterxml.jackson.core.JsonGenerator;
import eu.toolchain.scribe.Context;
import lombok.Data;

import java.io.IOException;

@Data
public class FloatStreamEncoder extends AbstractStreamEncoder<Float> {
  @Override
  public void streamEncode(
      final Context path, final Float instance, final JsonGenerator target
  ) {
    try {
      target.writeNumber(instance);
    } catch (final IOException e) {
      throw new RuntimeException(e);
    }
  }

  private static final FloatStreamEncoder INSTANCE = new FloatStreamEncoder();

  public static FloatStreamEncoder get() {
    return INSTANCE;
  }
}
