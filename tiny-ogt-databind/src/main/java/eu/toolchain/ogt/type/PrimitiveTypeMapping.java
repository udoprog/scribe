package eu.toolchain.ogt.type;

import java.io.IOException;

import eu.toolchain.ogt.Context;
import eu.toolchain.ogt.FieldDecoder;
import eu.toolchain.ogt.FieldEncoder;
import eu.toolchain.ogt.JavaType;
import eu.toolchain.ogt.PrimitiveType;
import lombok.Data;

@Data
public class PrimitiveTypeMapping implements TypeMapping {
    private final JavaType javaType;
    private final PrimitiveType type;

    @Override
    public JavaType getType() {
        return javaType;
    }

    @Override
    public Object decode(FieldDecoder accessor, Context path) {
        try {
            return type.get(accessor);
        } catch (final IOException e) {
            throw path.error("Failed to encode primitive", e);
        }
    }

    @Override
    public Object encode(FieldEncoder visitor, Context path, Object value) {
        try {
            return type.set(visitor, value);
        } catch (final IOException e) {
            throw path.error("Failed to encode primitive", e);
        }
    }

    @Override
    public String toString() {
        return type.toString();
    }
}
