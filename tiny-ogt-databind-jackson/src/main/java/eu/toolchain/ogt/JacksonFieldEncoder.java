package eu.toolchain.ogt;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.BaseEncoding;
import eu.toolchain.ogt.type.TypeMapping;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class JacksonFieldEncoder implements FieldEncoder<JsonNode> {
    private static final BaseEncoding BASE64 = JsonNode.StringJsonNode.BASE64;

    @Override
    public byte[] encodeBytesField(JavaType type, Object value) {
        throw new RuntimeException("Not supported");
    }

    @Override
    public JsonNode encodeBytes(byte[] bytes) throws IOException {
        return new JsonNode.StringJsonNode(BASE64.encode(bytes));
    }

    @Override
    public JsonNode encodeShort(short value) throws IOException {
        return new JsonNode.NumberJsonNode(value);
    }

    @Override
    public JsonNode encodeInteger(int value) throws IOException {
        return new JsonNode.NumberJsonNode(value);
    }

    @Override
    public JsonNode encodeLong(long value) throws IOException {
        return new JsonNode.NumberJsonNode(value);
    }

    @Override
    public JsonNode encodeFloat(float value) throws IOException {
        return new JsonNode.FloatJsonNode(value);
    }

    @Override
    public JsonNode encodeDouble(double value) throws IOException {
        return new JsonNode.FloatJsonNode(value);
    }

    @Override
    public JsonNode encodeBoolean(boolean value) throws IOException {
        return new JsonNode.BooleanJsonNode(value);
    }

    @Override
    public JsonNode encodeByte(byte value) throws IOException {
        return new JsonNode.StringJsonNode(BASE64.encode(new byte[]{value}));
    }

    @Override
    public JsonNode encodeCharacter(char value) throws IOException {
        return new JsonNode.StringJsonNode(new String(new char[]{value}));
    }

    @Override
    public JsonNode encodeDate(Date value) throws IOException {
        return new JsonNode.NumberJsonNode(value.getTime());
    }

    @Override
    public JsonNode encodeList(TypeMapping value, List<?> list, Context path) throws IOException {
        final ImmutableList.Builder<JsonNode> values = ImmutableList.builder();

        int index = 0;

        for (final Object v : list) {
            values.add((JsonNode) value.encode(this, path.push(index++), v));
        }

        return new JsonNode.ListJsonNode(values.build());
    }

    @Override
    public JsonNode encodeMap(TypeMapping key, TypeMapping value, Map<?, ?> map, Context path)
        throws IOException {
        if (!key.getType().getRawClass().equals(String.class)) {
            throw path.error("Keys must be strings");
        }

        @SuppressWarnings("unchecked") final Map<String, ?> input = (Map<String, ?>) map;
        final ImmutableMap.Builder<String, JsonNode> output = ImmutableMap.builder();

        for (final Map.Entry<String, ?> e : input.entrySet()) {
            output.put(e.getKey(),
                (JsonNode) value.encode(this, path.push(e.getKey()), e.getValue()));
        }

        return new JsonNode.ObjectJsonNode(output.build());
    }

    @Override
    public JsonNode encodeString(String string) throws IOException {
        return new JsonNode.StringJsonNode(string);
    }

    @Override
    public EntityEncoder<JsonNode> newEntityEncoder() {
        return new JacksonEntityEncoder();
    }
}
