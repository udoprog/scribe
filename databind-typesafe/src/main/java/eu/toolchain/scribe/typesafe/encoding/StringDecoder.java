package eu.toolchain.scribe.typesafe.encoding;

import com.typesafe.config.ConfigValue;
import eu.toolchain.scribe.Context;
import eu.toolchain.scribe.Decoded;
import eu.toolchain.scribe.Decoder;
import lombok.Data;

@Data
public class StringDecoder implements Decoder<ConfigValue, String> {
  @Override
  public Decoded<String> decode(final Context path, final ConfigValue instance) {
    switch (instance.valueType()) {
      case STRING:
        return Decoded.of((String) instance.unwrapped());
      case NULL:
        return Decoded.absent();
      default:
        throw new IllegalArgumentException("Expected string: " + instance);
    }
  }

  private static final StringDecoder INSTANCE = new StringDecoder();

  public static StringDecoder get() {
    return INSTANCE;
  }
}
