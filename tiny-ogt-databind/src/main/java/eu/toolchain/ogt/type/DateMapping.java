package eu.toolchain.ogt.type;

import java.io.IOException;
import java.util.Date;

import eu.toolchain.ogt.Context;
import eu.toolchain.ogt.FieldDecoder;
import eu.toolchain.ogt.FieldEncoder;
import eu.toolchain.ogt.JavaType;
import lombok.Data;

@Data
public class DateMapping implements TypeMapping {
    public static final JavaType type = JavaType.construct(Date.class);

    @Override
    public JavaType getType() {
        return type;
    }

    @Override
    public Object decode(FieldDecoder accessor, Context path) throws IOException {
        return accessor.asDate();
    }

    @Override
    public void encode(FieldEncoder visitor, Object string, Context path) throws IOException {
        visitor.setDate((Date) string);
    }

    @Override
    public String toString() {
        return "<date>";
    }
}
