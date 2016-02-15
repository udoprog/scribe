package eu.toolchain.ogt;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import eu.toolchain.ogt.type.TypeMapping;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class JacksonFieldDecoder implements FieldDecoder<JsonNode> {
    @Override
    public Object decodeBytesField(JavaType type, byte[] bytes) {
        throw new RuntimeException("not supported");
    }

    @Override
    public byte[] decodeBytes(JsonNode node) {
        return node.asBytes();
    }

    @Override
    public short decodeShort(JsonNode node) {
        return node.asShort();
    }

    @Override
    public int decodeInteger(JsonNode node) {
        return node.asInteger();
    }

    @Override
    public long decodeLong(JsonNode node) {
        return node.asLong();
    }

    @Override
    public float decodeFloat(JsonNode node) {
        return node.asFloat();
    }

    @Override
    public double decodeDouble(JsonNode node) {
        return node.asDouble();
    }

    @Override
    public boolean decodeBoolean(JsonNode node) {
        return node.asBoolean();
    }

    @Override
    public byte decodeByte(JsonNode node) {
        return node.asByte();
    }

    @Override
    public char decodeCharacter(JsonNode node) {
        return node.asCharacter();
    }

    @Override
    public Date decodeDate(JsonNode node) {
        return new Date(node.asLong());
    }

    @Override
    public List<?> decodeList(TypeMapping value, Context path, JsonNode node) throws IOException {
        final ImmutableList.Builder<Object> list = ImmutableList.builder();

        int index = 0;

        for (final JsonNode v : node.asList()) {
            list.add(value.decode(this, path.push(index++), v));
        }

        return list.build();
    }

    @Override
    public Map<?, ?> decodeMap(TypeMapping key, TypeMapping value, Context path, JsonNode node)
        throws IOException {
        final ImmutableMap.Builder<String, Object> map = ImmutableMap.builder();

        for (final Map.Entry<String, JsonNode> e : node.asObject().entrySet()) {
            final String k = e.getKey();
            final Object v = value.decode(this, path.push(k), e.getValue());
            map.put(k, v);
        }

        return map.build();
    }

    @Override
    public String decodeString(JsonNode node) {
        return node.asString();
    }

    @Override
    public EntityDecoder<JsonNode> newEntityDecoder() {
        return new JacksonEntityDecoder();
    }
}
