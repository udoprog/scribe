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
    public Object decode(FieldDecoder accessor, Context path) throws IOException {
        return accessor.asBytes();
    }

    @Override
    public void encode(FieldEncoder visitor, Object value, Context path) throws IOException {
        visitor.setBytes((byte[]) value);
    }

    @Override
    public String toString() {
        return "<byte[]>";
    }
}
