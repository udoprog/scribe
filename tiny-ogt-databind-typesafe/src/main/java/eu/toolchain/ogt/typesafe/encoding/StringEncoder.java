package eu.toolchain.ogt.typesafe.encoding;

import com.typesafe.config.ConfigValue;
import com.typesafe.config.ConfigValueFactory;
import eu.toolchain.ogt.Context;
import eu.toolchain.ogt.Encoder;
import lombok.Data;

@Data
public class StringEncoder implements Encoder<ConfigValue, String> {
    @Override
    public ConfigValue encode(final Context path, final String instance) {
        return ConfigValueFactory.fromAnyRef(instance);
    }
}
