package eu.toolchain.ogt.typesafe.encoding;

import com.typesafe.config.ConfigValue;
import eu.toolchain.ogt.Context;
import eu.toolchain.ogt.Decoder;
import lombok.Data;

@Data
public class FloatDecoder implements Decoder<ConfigValue, Float> {
    @Override
    public Float decode(final Context path, final ConfigValue instance) {
        switch (instance.valueType()) {
            case NUMBER:
                return ((Number) instance.unwrapped()).floatValue();
            default:
                throw new IllegalArgumentException("Expected number: " + instance);
        }
    }

    private static final FloatDecoder INSTANCE = new FloatDecoder();

    public static FloatDecoder get() {
        return INSTANCE;
    }
}
