package eu.toolchain.ogt.typesafe.encoding;

import com.typesafe.config.ConfigValue;
import com.typesafe.config.ConfigValueFactory;
import eu.toolchain.ogt.Context;
import eu.toolchain.ogt.Encoder;
import lombok.Data;

@Data
public class FloatEncoder implements Encoder<ConfigValue, Float> {
    @Override
    public ConfigValue encode(final Context path, final Float instance) {
        return ConfigValueFactory.fromAnyRef(instance);
    }

    private static final FloatEncoder INSTANCE = new FloatEncoder();

    public static FloatEncoder get() {
        return INSTANCE;
    }
}
