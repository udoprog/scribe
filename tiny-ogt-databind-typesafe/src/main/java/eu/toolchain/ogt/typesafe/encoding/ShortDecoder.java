package eu.toolchain.ogt.typesafe.encoding;

import com.typesafe.config.ConfigValue;
import eu.toolchain.ogt.Context;
import eu.toolchain.ogt.Decoder;
import lombok.Data;

@Data
public class ShortDecoder implements Decoder<ConfigValue, Short> {
    @Override
    public Short decode(final Context path, final ConfigValue instance) {
        switch (instance.valueType()) {
            case NUMBER:
                return ((Number) instance.unwrapped()).shortValue();
            default:
                throw new IllegalArgumentException("Expected number: " + instance);
        }
    }

    private static final ShortDecoder INSTANCE = new ShortDecoder();

    public static ShortDecoder get() {
        return INSTANCE;
    }
}
