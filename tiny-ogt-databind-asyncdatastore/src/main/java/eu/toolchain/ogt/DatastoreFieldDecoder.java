package eu.toolchain.ogt;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.spotify.asyncdatastoreclient.Entity;
import com.spotify.asyncdatastoreclient.Value;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import eu.toolchain.ogt.type.TypeMapping;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DatastoreFieldDecoder implements FieldDecoder {
    private final Value value;

    @Override
    public Object decode(JavaType type, byte[] bytes) {
        throw new RuntimeException("not supported");
    }

    @Override
    public byte[] asBytes() {
        return value.getBlob().toByteArray();
    }

    @Override
    public short asShort() {
        return (short) asLong();
    }

    @Override
    public int asInteger() {
        return (int) asLong();
    }

    @Override
    public long asLong() {
        return value.getInteger();
    }

    @Override
    public float asFloat() {
        return (float) asDouble();
    }

    @Override
    public double asDouble() {
        return value.getDouble();
    }

    @Override
    public boolean asBoolean() {
        return value.getBoolean();
    }

    @Override
    public byte asByte() {
        return value.getBlob().byteAt(0);
    }

    @Override
    public char asCharacter() {
        return value.getString().charAt(0);
    }

    @Override
    public Date asDate() {
        return value.getDate();
    }

    @Override
    public String asString() {
        return value.getString();
    }

    @Override
    public List<?> asList(TypeMapping value, Context path) throws IOException {
        final ImmutableList.Builder<Object> list = ImmutableList.builder();

        int index = 0;

        for (final Value v : this.value.getList()) {
            list.add(value.decode(new DatastoreFieldDecoder(v), path.push(index++)));
        }

        return list.build();
    }

    @Override
    public Map<?, ?> asMap(TypeMapping key, TypeMapping value, Context path) throws IOException {
        final ImmutableMap.Builder<String, Object> map = ImmutableMap.builder();

        final Entity entity = this.value.getEntity();

        for (final Map.Entry<String, Value> e : entity.getProperties().entrySet()) {
            final String k = e.getKey();
            final Object v = value.decode(new DatastoreFieldDecoder(e.getValue()), path.push(k));
            map.put(k, v);
        }

        return map.build();
    }

    @Override
    public EntityDecoder asEntity() {
        return new DatastoreEntityDecoder(value.getEntity());
    }
}
