package eu.toolchain.ogt.type;

import eu.toolchain.ogt.Context;
import eu.toolchain.ogt.JavaType;
import eu.toolchain.ogt.TypeDecoder;
import eu.toolchain.ogt.TypeEncoder;
import lombok.Data;

import java.io.IOException;

@Data
public class ByteArrayTypeMapping implements TypeMapping {
    public static final JavaType TYPE = JavaType.construct(byte[].class);

    @Override
    public JavaType getType() {
        return TYPE;
    }

    @Override
    public <T> Object decode(TypeDecoder<T> accessor, Context path, T instance) {
        try {
            return accessor.decodeBytes(instance);
        } catch (final IOException e) {
            throw path.error("Failed to decode bytes");
        }
    }

    @Override
    public <T> T encode(TypeEncoder<T> encoder, Context path, Object value) {
        try {
            return encoder.encodeBytes((byte[]) value);
        } catch (final IOException e) {
            throw path.error("Failed to encode bytes", e);
        }
    }

    @Override
    public String toString() {
        return "byte[]";
    }
}
