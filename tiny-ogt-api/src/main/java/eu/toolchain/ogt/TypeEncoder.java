package eu.toolchain.ogt;

import eu.toolchain.ogt.type.TypeMapping;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface TypeEncoder<T> {
    byte[] encodeBytesField(JavaType type, Object value) throws IOException;

    T encodeBytes(byte[] bytes) throws IOException;

    T encodeShort(short value) throws IOException;

    T encodeInteger(int value) throws IOException;

    T encodeLong(long value) throws IOException;

    T encodeFloat(float value) throws IOException;

    T encodeDouble(double value) throws IOException;

    T encodeBoolean(boolean value) throws IOException;

    T encodeByte(byte value) throws IOException;

    T encodeCharacter(char value) throws IOException;

    T encodeDate(Date value) throws IOException;

    T encodeString(String value) throws IOException;

    T encodeList(TypeMapping value, List<?> list, Context path) throws IOException;

    T encodeMap(TypeMapping key, TypeMapping value, Map<?, ?> map, Context path) throws IOException;

    EntityEncoder<T> newEntityEncoder();
}
