package eu.toolchain.ogt.typesafe.encoding;

import com.typesafe.config.ConfigValue;
import eu.toolchain.ogt.Context;
import eu.toolchain.ogt.Decoder;
import lombok.Data;

@Data
public class DoubleDecoder implements Decoder<ConfigValue, Double> {
    @Override
    public Double decode(final Context path, final ConfigValue instance) {
        switch (instance.valueType()) {
            case NUMBER:
                return ((Number) instance.unwrapped()).doubleValue();
            default:
                throw new IllegalArgumentException("Expected number: " + instance);
        }
    }

    private static final DoubleDecoder INSTANCE = new DoubleDecoder();

    public static DoubleDecoder get() {
        return INSTANCE;
    }
}
