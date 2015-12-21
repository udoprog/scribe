package eu.toolchain.ogt.type;

import java.io.IOException;

import eu.toolchain.ogt.Context;
import eu.toolchain.ogt.FieldDecoder;
import eu.toolchain.ogt.FieldEncoder;
import eu.toolchain.ogt.JavaType;
import lombok.Data;

@Data
public class EncodedBytesTypeMapping implements TypeMapping {
    public static final JavaType TYPE = JavaType.construct(byte[].class);

    private final JavaType type;

    @Override
    public JavaType getType() {
        return TYPE;
    }

    @Override
    public Object decode(FieldDecoder accessor, Context path) {
        try {
            return accessor.decode(type, accessor.decodeBytes());
        } catch (final IOException e) {
            throw path.error("Failed to decode bytes", e);
        }
    }

    @Override
    public Object encode(FieldEncoder visitor, Context path, Object value) {
        try {
            return visitor.encodeBytes(visitor.encode(type, value));
        } catch (final IOException e) {
            throw path.error("Failed to encode bytes", e);
        }
    }

    @Override
    public String toString() {
        return "<byte[]>";
    }
}
