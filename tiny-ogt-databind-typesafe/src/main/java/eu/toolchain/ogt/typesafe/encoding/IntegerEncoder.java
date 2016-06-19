package eu.toolchain.ogt.typesafe.encoding;

import com.typesafe.config.ConfigValue;
import com.typesafe.config.ConfigValueFactory;
import eu.toolchain.ogt.Context;
import eu.toolchain.ogt.Encoder;
import lombok.Data;

@Data
public class IntegerEncoder implements Encoder<ConfigValue, Integer> {
    @Override
    public ConfigValue encode(final Context path, final Integer instance) {
        return ConfigValueFactory.fromAnyRef(instance);
    }

    private static final IntegerEncoder INSTANCE = new IntegerEncoder();

    public static IntegerEncoder get() {
        return INSTANCE;
    }
}
