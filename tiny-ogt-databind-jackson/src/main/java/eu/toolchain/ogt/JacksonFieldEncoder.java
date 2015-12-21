package eu.toolchain.ogt;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
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

    @Override
    public byte[] encode(JavaType type, Object value) {
        throw new RuntimeException("Not supported");
    }

    @Override
    public Object encodeBytes(byte[] bytes) throws IOException {
        return new JsonNode.StringJsonNode(BASE64.encode(bytes));
    }

    @Override
    public Object encodeShort(short value) throws IOException {
        return new JsonNode.NumberJsonNode(value);
    }

    @Override
    public Object encodeInteger(int value) throws IOException {
        return new JsonNode.NumberJsonNode(value);
    }

    @Override
    public Object encodeLong(long value) throws IOException {
        return new JsonNode.NumberJsonNode(value);
    }

    @Override
    public Object encodeFloat(float value) throws IOException {
        return new JsonNode.FloatJsonNode(value);
    }

    @Override
    public Object encodeDouble(double value) throws IOException {
        return new JsonNode.FloatJsonNode(value);
    }

    @Override
    public Object encodeBoolean(boolean value) throws IOException {
        return new JsonNode.BooleanJsonNode(value);
    }

    @Override
    public Object encodeByte(byte value) throws IOException {
        return new JsonNode.StringJsonNode(BASE64.encode(new byte[] {value}));
    }

    @Override
    public Object encodeCharacter(char value) throws IOException {
        return new JsonNode.StringJsonNode(new String(new char[] {value}));
    }

    @Override
    public Object encodeDate(Date value) throws IOException {
        return new JsonNode.NumberJsonNode(value.getTime());
    }

    @Override
    public Object encodeList(TypeMapping value, List<?> list, Context path) throws IOException {
        final ImmutableList.Builder<JsonNode> values = ImmutableList.builder();

        int index = 0;

        for (final Object v : list) {
            values.add((JsonNode) value.encode(this, path.push(index++), v));
        }

        return new JsonNode.ListJsonNode(values.build());
    }

    @Override
    public Object encodeMap(TypeMapping key, TypeMapping value, Map<?, ?> map, Context path)
            throws IOException {
        if (!key.getType().getRawClass().equals(String.class)) {
            throw path.error("Keys must be strings");
        }

        @SuppressWarnings("unchecked")
        final Map<String, ?> input = (Map<String, ?>) map;
        final ImmutableMap.Builder<String, JsonNode> output = ImmutableMap.builder();

        for (final Map.Entry<String, ?> e : input.entrySet()) {
            output.put(e.getKey(),
                    (JsonNode) value.encode(this, path.push(e.getKey()), e.getValue()));
        }

        return new JsonNode.ObjectJsonNode(output.build());
    }

    @Override
    public Object encodeString(String string) throws IOException {
        return new JsonNode.StringJsonNode(string);
    }

    @Override
    public EntityEncoder encodeEntity() {
        return new JacksonEntityEncoder();
    }
}
