package eu.toolchain.scribe.typesafe.encoding;

import com.typesafe.config.ConfigValue;
import com.typesafe.config.ConfigValueType;
import eu.toolchain.scribe.Context;
import eu.toolchain.scribe.Decoded;
import eu.toolchain.scribe.Decoder;
import lombok.Data;

@Data
public class BooleanDecoder implements Decoder<ConfigValue, Boolean> {
  @Override
  public Decoded<Boolean> decode(final Context path, final ConfigValue instance) {
    if (instance.valueType() == ConfigValueType.NULL) {
      return Decoded.absent();
    }

    if (instance.valueType() != ConfigValueType.BOOLEAN) {
      throw new IllegalArgumentException("Expected boolean: " + instance);
    }

    return Decoded.of((Boolean) instance.unwrapped());
  }

  private static final BooleanDecoder INSTANCE = new BooleanDecoder();

  public static BooleanDecoder get() {
    return INSTANCE;
  }
}
