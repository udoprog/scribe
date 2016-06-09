package eu.toolchain.ogt.type;

import eu.toolchain.ogt.Context;
import eu.toolchain.ogt.JavaType;
import eu.toolchain.ogt.TypeDecoder;
import eu.toolchain.ogt.TypeEncoder;
import lombok.Data;

import java.io.IOException;

@Data
public class EncodedForeignBytesTypeMapping implements TypeMapping {
    public static final JavaType TYPE = JavaType.construct(byte[].class);

    private final JavaType type;

    @Override
    public JavaType getType() {
        return TYPE;
    }

    @Override
    public <T> Object decode(TypeDecoder<T> decoder, Context path, T instance) {
        try {
            return decoder.decodeForeignBytesField(type, decoder.decodeBytes(instance));
        } catch (final IOException e) {
            throw path.error("Failed to decode bytes", e);
        }
    }

    @Override
    public <T> T encode(TypeEncoder<T> encoder, Context path, Object value) {
        try {
            return encoder.encodeBytes(encoder.encodeForeignBytesField(type, value));
        } catch (final IOException e) {
            throw path.error("Failed to encode bytes", e);
        }
    }

    @Override
    public String toString() {
        return "<byte[]>";
    }
}
