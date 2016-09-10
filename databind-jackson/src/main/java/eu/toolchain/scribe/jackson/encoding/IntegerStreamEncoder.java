package eu.toolchain.scribe.jackson.encoding;

import com.fasterxml.jackson.core.JsonGenerator;
import eu.toolchain.scribe.Context;
import lombok.Data;

@Data
public class IntegerStreamEncoder extends AbstractStreamEncoder<Integer> {
  @Override
  public void streamEncode(
      final Context path, final Integer instance, final JsonGenerator target
  ) {
    try {
      target.writeNumber(instance);
    } catch (final Exception e) {
      throw path.error(e);
    }
  }

  private static final IntegerStreamEncoder INSTANCE = new IntegerStreamEncoder();

  public static IntegerStreamEncoder get() {
    return INSTANCE;
  }
}
