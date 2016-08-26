package eu.toolchain.scribe.typesafe.encoding;

import com.typesafe.config.ConfigValue;
import com.typesafe.config.ConfigValueFactory;
import eu.toolchain.scribe.Context;
import eu.toolchain.scribe.Encoder;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class MapEncoder<ValueSource> extends AbstractEncoder<Map<String, ValueSource>> {
  private final Encoder<ConfigValue, ValueSource> value;

  @Override
  public ConfigValue encode(final Context path, final Map<String, ValueSource> instance) {
    final Map<String, ConfigValue> result = new HashMap<>();

    for (final Map.Entry<String, ValueSource> e : instance.entrySet()) {
      final Context p = path.push(e.getKey());

      value.encodeOptionally(p, e.getValue(), target -> {
        result.put(e.getKey(), target);
      });
    }

    return ConfigValueFactory.fromMap(result);
  }
}
