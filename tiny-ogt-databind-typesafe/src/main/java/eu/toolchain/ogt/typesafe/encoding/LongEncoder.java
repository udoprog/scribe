package eu.toolchain.ogt.typesafe.encoding;

import com.typesafe.config.ConfigValue;
import com.typesafe.config.ConfigValueFactory;
import eu.toolchain.ogt.Context;
import eu.toolchain.ogt.Encoder;
import lombok.Data;

@Data
public class LongEncoder implements Encoder<ConfigValue, Long> {
    @Override
    public ConfigValue encode(final Context path, final Long instance) {
        return ConfigValueFactory.fromAnyRef(instance);
    }

    private static final LongEncoder INSTANCE = new LongEncoder();

    public static LongEncoder get() {
        return INSTANCE;
    }
}
