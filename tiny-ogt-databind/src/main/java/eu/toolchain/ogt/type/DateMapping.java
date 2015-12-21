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
    public Object decode(FieldDecoder accessor, Context path) {
        try {
            return accessor.decodeDate();
        } catch (final IOException e) {
            throw path.error("Failed to decode date", e);
        }
    }

    @Override
    public Object encode(FieldEncoder visitor, Context path, Object value) {
        try {
            return visitor.encodeDate((Date) value);
        } catch (final IOException e) {
            throw path.error("Failed to encode date", e);
        }
    }

    @Override
    public String toString() {
        return "<date>";
    }
}
