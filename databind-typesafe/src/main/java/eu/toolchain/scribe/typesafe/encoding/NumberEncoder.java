package eu.toolchain.scribe.typesafe.encoding;

import com.typesafe.config.ConfigValue;
import com.typesafe.config.ConfigValueFactory;
import eu.toolchain.scribe.Context;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class NumberEncoder extends AbstractEncoder<Number> {
  @Override
  public ConfigValue encode(final Context path, final Number instance) {
    return ConfigValueFactory.fromAnyRef(instance);
  }

  private static final NumberEncoder INSTANCE = new NumberEncoder();

  public static NumberEncoder get() {
    return INSTANCE;
  }
}
