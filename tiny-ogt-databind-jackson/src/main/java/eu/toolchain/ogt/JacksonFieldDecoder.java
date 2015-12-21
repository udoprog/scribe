package eu.toolchain.ogt;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import eu.toolchain.ogt.type.TypeMapping;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JacksonFieldDecoder implements FieldDecoder {
    private final JsonNode node;

    @Override
    public Object decode(JavaType type, byte[] bytes) {
        throw new RuntimeException("not supported");
    }

    @Override
    public byte[] decodeBytes() {
        return node.asBytes();
    }

    @Override
    public short decodeShort() {
        return node.asShort();
    }

    @Override
    public int decodeInteger() {
        return node.asInteger();
    }

    @Override
    public long decodeLong() {
        return node.asLong();
    }

    @Override
    public float decodeFloat() {
        return node.asFloat();
    }

    @Override
    public double decodeDouble() {
        return node.asDouble();
    }

    @Override
    public boolean decodeBoolean() {
        return node.asBoolean();
    }

    @Override
    public byte decodeByte() {
        return node.asByte();
    }

    @Override
    public char decodeCharacter() {
        return node.asCharacter();
    }

    @Override
    public Date decodeDate() {
        return new Date(node.asLong());
    }

    @Override
    public List<?> decodeList(TypeMapping value, Context path) throws IOException {
        final ImmutableList.Builder<Object> list = ImmutableList.builder();

        int index = 0;

        for (final JsonNode v : node.asList()) {
            list.add(value.decode(new JacksonFieldDecoder(v), path.push(index++)));
        }

        return list.build();
    }

    @Override
    public Map<?, ?> decodeMap(TypeMapping key, TypeMapping value, Context path) throws IOException {
        final ImmutableMap.Builder<String, Object> map = ImmutableMap.builder();

        for (final Map.Entry<String, JsonNode> e : node.asObject().entrySet()) {
            final String k = e.getKey();
            final Object v = value.decode(new JacksonFieldDecoder(e.getValue()), path.push(k));
            map.put(k, v);
        }

        return map.build();
    }

    @Override
    public String decodeString() {
        return node.asString();
    }

    @Override
    public EntityDecoder asEntity() {
        return new JacksonEntityDecoder(node);
    }
}
