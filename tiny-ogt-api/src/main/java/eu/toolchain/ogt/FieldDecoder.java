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

    byte[] asBytes() throws IOException;

    short asShort() throws IOException;

    int asInteger() throws IOException;

    long asLong() throws IOException;

    float asFloat() throws IOException;

    double asDouble() throws IOException;

    boolean asBoolean() throws IOException;

    byte asByte() throws IOException;

    char asCharacter() throws IOException;

    Date asDate() throws IOException;

    List<?> asList(TypeMapping value, Context path) throws IOException;

    Map<?, ?> asMap(TypeMapping key, TypeMapping value, Context path) throws IOException;

    String asString() throws IOException;

    EntityDecoder asEntity() throws IOException;
}
