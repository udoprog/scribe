package eu.toolchain.ogt;

import com.fasterxml.jackson.core.JsonGenerator;
import com.google.common.io.BaseEncoding;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import eu.toolchain.ogt.type.TypeMapping;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JacksonFieldEncoder implements FieldEncoder {
    private static final BaseEncoding BASE64 = JsonNode.StringJsonNode.BASE64;

    private final JsonGenerator generator;

    @Override
    public byte[] encode(JavaType type, Object value) {
        throw new RuntimeException("Not supported");
    }

    @Override
    public void setBytes(byte[] bytes) throws IOException {
        generator.writeString(BASE64.encode(bytes));
    }

    @Override
    public void setShort(short value) throws IOException {
        generator.writeNumber(value);
    }

    @Override
    public void setInteger(int value) throws IOException {
        generator.writeNumber(value);
    }

    @Override
    public void setLong(long value) throws IOException {
        generator.writeNumber(value);
    }

    @Override
    public void setFloat(float value) throws IOException {
        generator.writeNumber(value);
    }

    @Override
    public void setDouble(double value) throws IOException {
        generator.writeNumber(value);
    }

    @Override
    public void setBoolean(boolean value) throws IOException {
        generator.writeBoolean(value);
    }

    @Override
    public void setByte(byte value) throws IOException {
        generator.writeNumber(value);
    }

    @Override
    public void setCharacter(char value) throws IOException {
        generator.writeString(new char[] {value}, 0, 0);
    }

    @Override
    public void setDate(Date value) throws IOException {
        generator.writeNumber(value.getTime());
    }

    @Override
    public void setList(TypeMapping value, List<?> list, Context path) throws IOException {
        generator.writeStartArray();

        int index = 0;

        for (final Object v : list) {
            value.encode(this, v, path.push(index++));
        }

        generator.writeEndArray();
    }

    @Override
    public void setMap(TypeMapping key, TypeMapping value, Map<?, ?> map, Context path)
            throws IOException {
        if (!key.getType().getRawClass().equals(String.class)) {
            throw path.error("Keys must be strings");
        }

        @SuppressWarnings("unchecked")
        final Map<String, ?> stringMap = (Map<String, ?>) map;

        generator.writeStartObject();

        for (final Map.Entry<String, ?> e : stringMap.entrySet()) {
            final String k = e.getKey();
            generator.writeFieldName(k);
            value.encode(this, e.getValue(), path.push(e.getKey()));
        }

        generator.writeEndObject();
    }

    @Override
    public void setString(String string) throws IOException {
        generator.writeString(string);
    }

    @Override
    public EntityEncoder setEntity() {
        return new JacksonEntityEncoder(generator);
    }
}
