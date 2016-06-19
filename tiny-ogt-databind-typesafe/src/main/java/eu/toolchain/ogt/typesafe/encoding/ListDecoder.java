package eu.toolchain.ogt.typesafe.encoding;

import com.google.common.collect.ImmutableList;
import com.typesafe.config.ConfigList;
import com.typesafe.config.ConfigValue;
import eu.toolchain.ogt.Context;
import eu.toolchain.ogt.Decoder;
import lombok.Data;

import java.util.List;

@Data
public class ListDecoder<ElementSource> implements Decoder<ConfigValue, List<ElementSource>> {
    private final Decoder<ConfigValue, ElementSource> value;

    @Override
    public List<ElementSource> decode(final Context path, final ConfigValue instance) {
        final List<ConfigValue> values;

        switch (instance.valueType()) {
            case LIST:
                values = ((ConfigList) instance);
                break;
            default:
                throw new IllegalArgumentException("Expected list: " + instance);
        }

        final ImmutableList.Builder<ElementSource> result = ImmutableList.builder();

        int index = 0;

        for (final ConfigValue value : values) {
            final Context p = path.push(index++);
            result.add(this.value.decode(p, value));
        }

        return result.build();
    }
}
