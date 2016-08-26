package eu.toolchain.scribe.typesafe.encoding;

import com.typesafe.config.ConfigValue;
import com.typesafe.config.ConfigValueFactory;
import eu.toolchain.scribe.Context;
import lombok.Data;

@Data
public class StringEncoder extends AbstractEncoder<String> {
  @Override
  public ConfigValue encode(final Context path, final String instance) {
    return ConfigValueFactory.fromAnyRef(instance);
  }

  private static final StringEncoder INSTANCE = new StringEncoder();

  public static StringEncoder get() {
    return INSTANCE;
  }
}
