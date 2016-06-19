package eu.toolchain.ogt.typesafe.encoding;

import com.typesafe.config.ConfigValue;
import com.typesafe.config.ConfigValueFactory;
import eu.toolchain.ogt.Context;
import eu.toolchain.ogt.Encoder;
import lombok.Data;

@Data
public class DoubleEncoder implements Encoder<ConfigValue, Double> {
    @Override
    public ConfigValue encode(final Context path, final Double instance) {
        return ConfigValueFactory.fromAnyRef(instance);
    }

    private static final DoubleEncoder INSTANCE = new DoubleEncoder();

    public static DoubleEncoder get() {
        return INSTANCE;
    }
}
