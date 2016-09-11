package eu.toolchain.scribe.jackson.encoding;

import com.fasterxml.jackson.core.JsonGenerator;
import eu.toolchain.scribe.Context;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class LongStreamEncoder extends AbstractStreamEncoder<Long> {
  @Override
  public void streamEncode(
      final Context path, final Long instance, final JsonGenerator target
  ) {
    try {
      target.writeNumber(instance);
    } catch (final Exception e) {
      throw path.error(e);
    }
  }

  private static final LongStreamEncoder INSTANCE = new LongStreamEncoder();

  public static LongStreamEncoder get() {
    return INSTANCE;
  }
}
