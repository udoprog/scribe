package eu.toolchain.scribe.jackson.encoding;

import com.fasterxml.jackson.core.JsonGenerator;
import eu.toolchain.scribe.Context;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ShortStreamEncoder extends AbstractStreamEncoder<Short> {
  @Override
  public void streamEncode(
      final Context path, final Short instance, final JsonGenerator target
  ) {
    try {
      target.writeNumber(instance);
    } catch (final Exception e) {
      throw path.error(e);
    }
  }

  private static final ShortStreamEncoder INSTANCE = new ShortStreamEncoder();

  public static ShortStreamEncoder get() {
    return INSTANCE;
  }
}
