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

    void setBytes(byte[] bytes) throws IOException;

    void setShort(short value) throws IOException;

    void setInteger(int value) throws IOException;

    void setLong(long value) throws IOException;

    void setFloat(float value) throws IOException;

    void setDouble(double value) throws IOException;

    void setBoolean(boolean value) throws IOException;

    void setByte(byte value) throws IOException;

    void setCharacter(char value) throws IOException;

    void setDate(Date value) throws IOException;

    void setString(String string) throws IOException;

    void setList(TypeMapping value, List<?> list, Context path) throws IOException;

    void setMap(TypeMapping key, TypeMapping value, Map<?, ?> map, Context path) throws IOException;

    EntityEncoder setEntity() throws IOException;
}
