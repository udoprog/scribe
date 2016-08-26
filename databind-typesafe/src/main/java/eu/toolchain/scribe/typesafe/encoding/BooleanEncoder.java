package eu.toolchain.scribe.typesafe.encoding;

import com.typesafe.config.ConfigValue;
import com.typesafe.config.ConfigValueFactory;
import eu.toolchain.scribe.Context;
import lombok.Data;

@Data
public class BooleanEncoder extends AbstractEncoder<Boolean> {
  @Override
  public ConfigValue encode(final Context path, final Boolean instance) {
    return ConfigValueFactory.fromAnyRef(instance);
  }

  private static final BooleanEncoder INSTANCE = new BooleanEncoder();

  public static BooleanEncoder get() {
    return INSTANCE;
  }
}
