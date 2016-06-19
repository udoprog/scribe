package eu.toolchain.ogt.typesafe.encoding;

import com.typesafe.config.ConfigValue;
import com.typesafe.config.ConfigValueFactory;
import eu.toolchain.ogt.Context;
import eu.toolchain.ogt.Encoder;
import lombok.Data;

@Data
public class ShortEncoder implements Encoder<ConfigValue, Short> {
    @Override
    public ConfigValue encode(final Context path, final Short instance) {
        return ConfigValueFactory.fromAnyRef(instance);
    }

    private static final ShortEncoder INSTANCE = new ShortEncoder();

    public static ShortEncoder get() {
        return INSTANCE;
    }
}
