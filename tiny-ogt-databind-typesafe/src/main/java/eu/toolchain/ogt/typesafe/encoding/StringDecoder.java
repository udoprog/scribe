package eu.toolchain.ogt.typesafe.encoding;

import com.typesafe.config.ConfigValue;
import eu.toolchain.ogt.Context;
import eu.toolchain.ogt.Decoder;
import lombok.Data;

@Data
public class StringDecoder implements Decoder<ConfigValue, String> {
    @Override
    public String decode(final Context path, final ConfigValue instance) {
        switch (instance.valueType()) {
            case STRING:
                return (String) instance.unwrapped();
            default:
                throw new IllegalArgumentException("Expected number: " + instance);
        }
    }
}
