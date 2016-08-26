package eu.toolchain.scribe.jackson.encoding;

import com.fasterxml.jackson.core.JsonGenerator;
import eu.toolchain.scribe.Context;
import lombok.Data;

import java.io.IOException;

@Data
public class StringStreamEncoder extends AbstractStreamEncoder<String> {
  @Override
  public void streamEncode(
      final Context path, final String instance, final JsonGenerator target
  ) {
    try {
      target.writeString(instance);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private static final StringStreamEncoder INSTANCE = new StringStreamEncoder();

  public static StringStreamEncoder get() {
    return INSTANCE;
  }
}
