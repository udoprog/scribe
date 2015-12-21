package eu.toolchain.ogt;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import eu.toolchain.ogt.type.TypeMapping;

public interface FieldDecoder {
    /**
     * Decode a value annotated with {@code eu.toolchain.ogt.annotations.Bytes}.
     *
     * @param mapping The mapping of the type.
     * @param bytes The bytes of the type.
     * @return A decoded instance of the given type.
     */
    Object decode(JavaType type, byte[] bytes) throws IOException;

    byte[] decodeBytes() throws IOException;

    short decodeShort() throws IOException;

    int decodeInteger() throws IOException;

    long decodeLong() throws IOException;

    float decodeFloat() throws IOException;

    double decodeDouble() throws IOException;

    boolean decodeBoolean() throws IOException;

    byte decodeByte() throws IOException;

    char decodeCharacter() throws IOException;

    Date decodeDate() throws IOException;

    String decodeString() throws IOException;

    List<?> decodeList(TypeMapping value, Context path) throws IOException;

    Map<?, ?> decodeMap(TypeMapping key, TypeMapping value, Context path) throws IOException;

    EntityDecoder asEntity() throws IOException;
}
