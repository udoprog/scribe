package eu.toolchain.ogt.typesafe;

import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigValue;
import eu.toolchain.ogt.Context;
import eu.toolchain.ogt.Decoder;
import eu.toolchain.ogt.Encoder;
import eu.toolchain.ogt.Encoding;
import eu.toolchain.ogt.StringEncoding;
import lombok.Data;

@Data
public class TypeSafeEncoding<T> implements StringEncoding<T>, Encoding<ConfigValue, T> {
    private final Encoder<ConfigValue, T> parentEncoder;
    private final Decoder<ConfigValue, T> parentDecoder;

    @Override
    public ConfigValue encode(T instance) {
        return parentEncoder.encode(Context.ROOT, instance);
    }

    @Override
    public T decode(ConfigValue instance) {
        return parentDecoder.decode(Context.ROOT, instance);
    }

    @Override
    public T decodeFromString(String json) {
        return decode(ConfigFactory.parseString(json).root());
    }

    @Override
    public String encodeAsString(T instance) {
        return encode(instance).render();
    }
}
