package eu.toolchain.ogt.typesafe.encoding;

import com.typesafe.config.ConfigValue;
import eu.toolchain.ogt.Context;
import eu.toolchain.ogt.Decoder;
import lombok.Data;

@Data
public class IntegerDecoder implements Decoder<ConfigValue, Integer> {
    @Override
    public Integer decode(final Context path, final ConfigValue instance) {
        switch (instance.valueType()) {
            case NUMBER:
                return ((Number) instance.unwrapped()).intValue();
            default:
                throw new IllegalArgumentException("Expected number: " + instance);
        }
    }

    private static final IntegerDecoder INSTANCE = new IntegerDecoder();

    public static IntegerDecoder get() {
        return INSTANCE;
    }
}
