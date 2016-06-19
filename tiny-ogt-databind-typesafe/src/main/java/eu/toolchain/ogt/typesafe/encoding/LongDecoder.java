package eu.toolchain.ogt.typesafe.encoding;

import com.typesafe.config.ConfigValue;
import eu.toolchain.ogt.Context;
import eu.toolchain.ogt.Decoder;
import lombok.Data;

@Data
public class LongDecoder implements Decoder<ConfigValue, Long> {
    @Override
    public Long decode(final Context path, final ConfigValue instance) {
        switch (instance.valueType()) {
            case NUMBER:
                return ((Number) instance.unwrapped()).longValue();
            default:
                throw new IllegalArgumentException("Expected number: " + instance);
        }
    }

    private static final LongDecoder INSTANCE = new LongDecoder();

    public static LongDecoder get() {
        return INSTANCE;
    }
}
