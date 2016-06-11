package eu.toolchain.ogt.type;

import eu.toolchain.ogt.Context;
import eu.toolchain.ogt.TypeDecoder;
import eu.toolchain.ogt.TypeEncoder;
import eu.toolchain.ogt.JavaType;
import eu.toolchain.ogt.PrimitiveType;
import lombok.Data;

import java.io.IOException;

@Data
public class PrimitiveTypeMapping implements TypeMapping {
    private final JavaType javaType;
    private final PrimitiveType type;

    @Override
    public JavaType getType() {
        return javaType;
    }

    @Override
    public <T> Object decode(TypeDecoder<T> decoder, Context path, T instance) {
        try {
            return type.get(decoder, instance);
        } catch (final Exception e) {
            throw path.error("Failed to encode primitive", e);
        }
    }

    @Override
    public <T> T encode(TypeEncoder<T> visitor, Context path, Object value) {
        try {
            return type.set(visitor, value);
        } catch (final Exception e) {
            throw path.error("Failed to encode primitive", e);
        }
    }

    @Override
    public String toString() {
        return type.toString();
    }
}
