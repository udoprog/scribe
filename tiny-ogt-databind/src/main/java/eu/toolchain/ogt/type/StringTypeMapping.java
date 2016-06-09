package eu.toolchain.ogt.type;

import eu.toolchain.ogt.Context;
import eu.toolchain.ogt.TypeDecoder;
import eu.toolchain.ogt.TypeEncoder;
import eu.toolchain.ogt.JavaType;
import lombok.Data;

import java.io.IOException;

@Data
public class StringTypeMapping implements TypeMapping {
    public static final JavaType TYPE = JavaType.construct(String.class);

    @Override
    public JavaType getType() {
        return TYPE;
    }

    @Override
    public <T> Object decode(TypeDecoder<T> decoder, Context path, T instance) {
        try {
            return decoder.decodeString(instance);
        } catch (final IOException e) {
            throw path.error("Failed to decode string", e);
        }
    }

    @Override
    public Object encode(TypeEncoder encoder, Context path, Object value) {
        try {
            return encoder.encodeString((String) value);
        } catch (final IOException e) {
            throw path.error("Failed to encode string", e);
        }
    }

    @Override
    public String toString() {
        return "<string>";
    }
}
