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
    private final TypeEncodingProvider<byte[]> bytesEncoding;
    private final Value value;

    @Override
    public Object decode(JavaType type, byte[] bytes) {
        return bytesEncoding.encodingFor(type).decode(bytes);
    }

    @Override
    public byte[] decodeBytes() {
        return value.getBlob().toByteArray();
    }

    @Override
    public short decodeShort() {
        return (short) decodeLong();
    }

    @Override
    public int decodeInteger() {
        return (int) decodeLong();
    }

    @Override
    public long decodeLong() {
        return value.getInteger();
    }

    @Override
    public float decodeFloat() {
        return (float) decodeDouble();
    }

    @Override
    public double decodeDouble() {
        return value.getDouble();
    }

    @Override
    public boolean decodeBoolean() {
        return value.getBoolean();
    }

    @Override
    public byte decodeByte() {
        return value.getBlob().byteAt(0);
    }

    @Override
    public char decodeCharacter() {
        return value.getString().charAt(0);
    }

    @Override
    public Date decodeDate() {
        return value.getDate();
    }

    @Override
    public String decodeString() {
        return value.getString();
    }

    @Override
    public List<?> decodeList(TypeMapping value, Context path) throws IOException {
        final ImmutableList.Builder<Object> list = ImmutableList.builder();

        int index = 0;

        for (final Value v : this.value.getList()) {
            list.add(value.decode(new DatastoreFieldDecoder(bytesEncoding, v), path.push(index++)));
        }

        return list.build();
    }

    @Override
    public Map<?, ?> decodeMap(TypeMapping key, TypeMapping value, Context path)
            throws IOException {
        final ImmutableMap.Builder<String, Object> map = ImmutableMap.builder();

        final Entity entity = this.value.getEntity();

        for (final Map.Entry<String, Value> e : entity.getProperties().entrySet()) {
            final String k = e.getKey();
            final Object v = value.decode(new DatastoreFieldDecoder(bytesEncoding, e.getValue()),
                    path.push(k));
            map.put(k, v);
        }

        return map.build();
    }

    @Override
    public EntityDecoder asEntity() {
        return new DatastoreEntityDecoder(bytesEncoding, value.getEntity());
    }
}
