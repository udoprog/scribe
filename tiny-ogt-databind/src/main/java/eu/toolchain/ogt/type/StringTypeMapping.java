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
    public Object decode(FieldDecoder accessor, Context path) throws IOException {
        return accessor.asString();
    }

    @Override
    public void encode(FieldEncoder visitor, Object string, Context path) throws IOException {
        visitor.setString((String) string);
    }

    @Override
    public String toString() {
        return "<string>";
    }
}
