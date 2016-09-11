package eu.toolchain.scribe.typesafe.encoding;

import com.typesafe.config.ConfigValue;
import com.typesafe.config.ConfigValueFactory;
import eu.toolchain.scribe.Context;
import eu.toolchain.scribe.Encoder;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public abstract class AbstractEncoder<T> implements Encoder<ConfigValue, T> {
  @Override
  public ConfigValue encodeEmpty(final Context path) {
    return ConfigValueFactory.fromAnyRef(null);
  }
}
