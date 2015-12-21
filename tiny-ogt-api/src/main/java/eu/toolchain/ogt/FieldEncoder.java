package eu.toolchain.ogt;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import eu.toolchain.ogt.type.TypeMapping;

public interface FieldEncoder {
    /**
     * Encode an instance of the given type annotated with
     * {@code eu.toolchain.ogt.annotations.Bytes}.
     *
     * @param mapping The type to encode.
     * @param value The value to encode.
     * @return A byte array representing the instance.
     */
    byte[] encode(JavaType type, Object value) throws IOException;

    Object encodeBytes(byte[] bytes) throws IOException;

    Object encodeShort(short value) throws IOException;

    Object encodeInteger(int value) throws IOException;

    Object encodeLong(long value) throws IOException;

    Object encodeFloat(float value) throws IOException;

    Object encodeDouble(double value) throws IOException;

    Object encodeBoolean(boolean value) throws IOException;

    Object encodeByte(byte value) throws IOException;

    Object encodeCharacter(char value) throws IOException;

    Object encodeDate(Date value) throws IOException;

    Object encodeString(String value) throws IOException;

    Object encodeList(TypeMapping value, List<?> list, Context path) throws IOException;

    Object encodeMap(TypeMapping key, TypeMapping value, Map<?, ?> map, Context path)
            throws IOException;

    EntityEncoder encodeEntity();

    default Object filter(Object value) {
        return value;
    }
}
