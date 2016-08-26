package eu.toolchain.scribe.typesafe.encoding;

import com.typesafe.config.ConfigObject;
import com.typesafe.config.ConfigValue;
import eu.toolchain.scribe.Context;
import eu.toolchain.scribe.Decoded;
import eu.toolchain.scribe.Decoder;
import lombok.Data;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Data
public class MapDecoder<ValueSource> implements Decoder<ConfigValue, Map<String, ValueSource>> {
  private final Decoder<ConfigValue, ValueSource> value;

  @Override
  public Decoded<Map<String, ValueSource>> decode(
      final Context path, final ConfigValue instance
  ) {
    final Map<String, ConfigValue> values;

    switch (instance.valueType()) {
      case OBJECT:
        values = ((ConfigObject) instance);
        break;
      case NULL:
        return Decoded.absent();
      default:
        throw new IllegalArgumentException("Expected object: " + instance);
    }

    final Map<String, ValueSource> result = new HashMap<>();

    for (final Map.Entry<String, ConfigValue> e : values.entrySet()) {
      final Context p = path.push(e.getKey());

      value.decode(p, e.getValue()).ifPresent(v -> {
        result.put(e.getKey(), v);
      });
    }

    return Decoded.of(Collections.unmodifiableMap(result));
  }
}
