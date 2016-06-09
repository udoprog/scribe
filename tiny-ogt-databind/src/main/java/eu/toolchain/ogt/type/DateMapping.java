package eu.toolchain.ogt.type;

import eu.toolchain.ogt.Context;
import eu.toolchain.ogt.TypeDecoder;
import eu.toolchain.ogt.TypeEncoder;
import eu.toolchain.ogt.JavaType;
import lombok.Data;

import java.io.IOException;
import java.util.Date;

@Data
public class DateMapping implements TypeMapping {
    public static final JavaType type = JavaType.construct(Date.class);

    @Override
    public JavaType getType() {
        return type;
    }

    @Override
    public <T> Object decode(TypeDecoder<T> accessor, Context path, T instance) {
        try {
            return accessor.decodeDate(instance);
        } catch (final IOException e) {
            throw path.error("Failed to decode date", e);
        }
    }

    @Override
    public <T> T encode(TypeEncoder<T> visitor, Context path, Object value) {
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
