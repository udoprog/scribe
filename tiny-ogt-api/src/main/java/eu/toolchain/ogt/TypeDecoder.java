package eu.toolchain.ogt;

import eu.toolchain.ogt.type.TypeMapping;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface TypeDecoder<T> {
    Object decodeBytesField(JavaType type, byte[] bytes) throws IOException;

    byte[] decodeBytes(T input) throws IOException;

    short decodeShort(T input) throws IOException;

    int decodeInteger(T input) throws IOException;

    long decodeLong(T input) throws IOException;

    float decodeFloat(T input) throws IOException;

    double decodeDouble(T input) throws IOException;

    boolean decodeBoolean(T input) throws IOException;

    byte decodeByte(T input) throws IOException;

    char decodeCharacter(T input) throws IOException;

    Date decodeDate(T input) throws IOException;

    String decodeString(T input) throws IOException;

    List<?> decodeList(TypeMapping value, Context path, T input) throws IOException;

    Map<?, ?> decodeMap(TypeMapping key, TypeMapping value, Context path, T input)
        throws IOException;

    EntityDecoder<T> decodeEntity(T input);
}
