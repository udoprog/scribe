package eu.toolchain.ogt.typesafe.encoding;

import com.google.common.collect.ImmutableMap;
import com.typesafe.config.ConfigValue;
import com.typesafe.config.ConfigValueFactory;
import eu.toolchain.ogt.Context;
import eu.toolchain.ogt.Encoder;
import lombok.Data;

import java.util.Map;

@Data
public class MapEncoder<ValueSource> implements Encoder<ConfigValue, Map<String, ValueSource>> {
    private final Encoder<ConfigValue, ValueSource> value;

    @Override
    public ConfigValue encode(final Context path, final Map<String, ValueSource> instance) {
        final ImmutableMap.Builder<String, ConfigValue> result = ImmutableMap.builder();

        for (final Map.Entry<String, ValueSource> e : instance.entrySet()) {
            final Context p = path.push(e.getKey());
            result.put(e.getKey(), value.encode(p, e.getValue()));
        }

        return ConfigValueFactory.fromMap(result.build());
    }
}
