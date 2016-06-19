package eu.toolchain.ogt.typesafe.encoding;

import com.google.common.collect.ImmutableMap;
import com.typesafe.config.ConfigObject;
import com.typesafe.config.ConfigValue;
import eu.toolchain.ogt.Context;
import eu.toolchain.ogt.Decoder;
import lombok.Data;

import java.util.Map;

@Data
public class MapDecoder<ValueSource> implements Decoder<ConfigValue, Map<String, ValueSource>> {
    private final Decoder<ConfigValue, ValueSource> value;

    @Override
    public Map<String, ValueSource> decode(final Context path, final ConfigValue instance) {
        final Map<String, ConfigValue> values;

        switch (instance.valueType()) {
            case OBJECT:
                values = ((ConfigObject) instance);
                break;
            default:
                throw new IllegalArgumentException("Expected object: " + instance);
        }

        final ImmutableMap.Builder<String, ValueSource> result = ImmutableMap.builder();

        for (final Map.Entry<String, ConfigValue> e : values.entrySet()) {
            final Context p = path.push(e.getKey());
            result.put(e.getKey(), value.decode(p, e.getValue()));
        }

        return result.build();
    }
}
