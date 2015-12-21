package eu.toolchain.ogt.type;

import java.io.IOException;

import eu.toolchain.ogt.Context;
import eu.toolchain.ogt.FieldDecoder;
import eu.toolchain.ogt.FieldEncoder;
import eu.toolchain.ogt.JavaType;
import lombok.Data;

@Data
public class BytesTypeMapping implements TypeMapping {
    public static final JavaType TYPE = JavaType.construct(byte[].class);

    @Override
    public JavaType getType() {
        return TYPE;
    }

    @Override
    public <T> Object decode(FieldDecoder<T> accessor, Context path, T instance) {
        try {
            return accessor.decodeBytes(instance);
        } catch (final IOException e) {
            throw path.error("Failed to decode bytes");
        }
    }

    @Override
    public <T> T encode(FieldEncoder<T> encoder, Context path, Object value) {
        try {
            return encoder.encodeBytes((byte[]) value);
        } catch (final IOException e) {
            throw path.error("Failed to encode bytes", e);
        }
    }

    @Override
    public String toString() {
        return "<byte[]>";
    }
}
