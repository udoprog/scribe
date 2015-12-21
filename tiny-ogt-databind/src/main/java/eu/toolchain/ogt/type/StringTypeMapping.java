package eu.toolchain.ogt.type;

import java.io.IOException;

import eu.toolchain.ogt.Context;
import eu.toolchain.ogt.FieldDecoder;
import eu.toolchain.ogt.FieldEncoder;
import eu.toolchain.ogt.JavaType;
import lombok.Data;

@Data
public class StringTypeMapping implements TypeMapping {
    public static final JavaType TYPE = JavaType.construct(String.class);

    @Override
    public JavaType getType() {
        return TYPE;
    }

    @Override
    public Object decode(FieldDecoder accessor, Context path) {
        try {
            return accessor.decodeString();
        } catch (final IOException e) {
            throw path.error("Failed to decode string", e);
        }
    }

    @Override
    public Object encode(FieldEncoder encoder, Context path, Object value) {
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
