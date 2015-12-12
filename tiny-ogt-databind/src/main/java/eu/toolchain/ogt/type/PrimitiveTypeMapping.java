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
    public Object decode(FieldDecoder accessor, Context path) throws IOException {
        return type.get(accessor);
    }

    @Override
    public void encode(FieldEncoder visitor, Object value, Context path) throws IOException {
        type.set(visitor, value);
    }

    @Override
    public String toString() {
        return type.toString();
    }
}
