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
    public byte[] asBytes() {
        return node.asBytes();
    }

    @Override
    public short asShort() {
        return node.asShort();
    }

    @Override
    public int asInteger() {
        return node.asInteger();
    }

    @Override
    public long asLong() {
        return node.asLong();
    }

    @Override
    public float asFloat() {
        return node.asFloat();
    }

    @Override
    public double asDouble() {
        return node.asDouble();
    }

    @Override
    public boolean asBoolean() {
        return node.asBoolean();
    }

    @Override
    public byte asByte() {
        return node.asByte();
    }

    @Override
    public char asCharacter() {
        return node.asCharacter();
    }

    @Override
    public Date asDate() {
        return new Date(node.asLong());
    }

    @Override
    public List<?> asList(TypeMapping value, Context path) throws IOException {
        final ImmutableList.Builder<Object> list = ImmutableList.builder();

        int index = 0;

        for (final JsonNode v : node.asList()) {
            list.add(value.decode(new JacksonFieldDecoder(v), path.push(index++)));
        }

        return list.build();
    }

    @Override
    public Map<?, ?> asMap(TypeMapping key, TypeMapping value, Context path) throws IOException {
        final ImmutableMap.Builder<String, Object> map = ImmutableMap.builder();

        for (final Map.Entry<String, JsonNode> e : node.asObject().entrySet()) {
            final String k = e.getKey();
            final Object v = value.decode(new JacksonFieldDecoder(e.getValue()), path.push(k));
            map.put(k, v);
        }

        return map.build();
    }

    @Override
    public String asString() {
        return node.asString();
    }

    @Override
    public EntityDecoder asEntity() {
        return new JacksonEntityDecoder(node);
    }
}
