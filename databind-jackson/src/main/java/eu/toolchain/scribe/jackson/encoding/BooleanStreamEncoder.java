package eu.toolchain.scribe.jackson.encoding;

import com.fasterxml.jackson.core.JsonGenerator;
import eu.toolchain.scribe.Context;
import lombok.Data;

import java.io.IOException;

@Data
public class BooleanStreamEncoder extends AbstractStreamEncoder<Boolean> {
  @Override
  public void streamEncode(
      final Context path, final Boolean instance, final JsonGenerator generator
  ) {
    try {
      generator.writeBoolean(instance);
    } catch (IOException e) {
      throw path.error("failed to write boolean", e);
    }
  }

  private static final BooleanStreamEncoder INSTANCE = new BooleanStreamEncoder();

  public static BooleanStreamEncoder get() {
    return INSTANCE;
  }
}
