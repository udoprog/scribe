package eu.toolchain.ogt.typesafe.encoding;

import com.google.common.collect.ImmutableList;
import com.typesafe.config.ConfigValue;
import com.typesafe.config.ConfigValueFactory;
import eu.toolchain.ogt.Context;
import eu.toolchain.ogt.Encoder;
import lombok.Data;

import java.util.List;

@Data
public class ListEncoder<ElementSource> implements Encoder<ConfigValue, List<ElementSource>> {
    private final Encoder<ConfigValue, ElementSource> value;

    @Override
    public ConfigValue encode(final Context path, final List<ElementSource> instance) {
        final ImmutableList.Builder<ConfigValue> result = ImmutableList.builder();

        int index = 0;

        for (final ElementSource value : instance) {
            final Context p = path.push(index++);
            result.add(this.value.encode(p, value));
        }

        return ConfigValueFactory.fromIterable(result.build());
    }
}
