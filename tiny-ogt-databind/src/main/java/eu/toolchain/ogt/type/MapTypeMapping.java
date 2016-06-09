package eu.toolchain.ogt.type;

import eu.toolchain.ogt.Context;
import eu.toolchain.ogt.EntityResolver;
import eu.toolchain.ogt.TypeDecoder;
import eu.toolchain.ogt.TypeEncoder;
import eu.toolchain.ogt.JavaType;
import lombok.Data;

import java.io.IOException;
import java.util.Map;

@Data
public class MapTypeMapping implements TypeMapping {
    private final JavaType javaType;
    private final TypeMapping key;
    private final TypeMapping value;

    @Override
    public String toString() {
        return "map<" + key + "=" + value + ">";
    }

    @Override
    public JavaType getType() {
        return javaType;
    }

    @Override
    public <T> Object decode(TypeDecoder<T> decoder, Context path, T instance) {
        try {
            return decoder.decodeMap(key, value, path, instance);
        } catch (final IOException e) {
            throw path.error("Failed to decode map", e);
        }
    }

    @Override
    public <T> T encode(TypeEncoder<T> encoder, Context path, Object map) {
        try {
            return encoder.encodeMap(key, value, (Map<?, ?>) map, path);
        } catch (final IOException e) {
            throw path.error("Failed to encode map", e);
        }
    }

    @Override
    public void initialize(final EntityResolver resolver) {
        key.initialize(resolver);
        value.initialize(resolver);
    }
}
