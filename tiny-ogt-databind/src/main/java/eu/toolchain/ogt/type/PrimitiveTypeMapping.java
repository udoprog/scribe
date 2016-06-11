package eu.toolchain.ogt.type;

import eu.toolchain.ogt.Context;
import eu.toolchain.ogt.JavaType;
import eu.toolchain.ogt.PrimitiveType;
import eu.toolchain.ogt.TypeDecoder;
import eu.toolchain.ogt.TypeEncoder;
import lombok.Data;

@Data
public class PrimitiveTypeMapping implements TypeMapping {
    private final JavaType type;
    private final PrimitiveType primitiveType;

    @Override
    public JavaType getType() {
        return type;
    }

    @Override
    public <T> Object decode(TypeDecoder<T> decoder, Context path, T instance) {
        try {
            return primitiveType.get(decoder, instance);
        } catch (final Exception e) {
            throw path.error("Failed to encode primitive", e);
        }
    }

    @Override
    public <T> T encode(TypeEncoder<T> visitor, Context path, Object value) {
        try {
            return primitiveType.set(visitor, value);
        } catch (final Exception e) {
            throw path.error("Failed to encode primitive", e);
        }
    }

    @Override
    public String toString() {
        return primitiveType.toString();
    }
}
