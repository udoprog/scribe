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
public class DatastoreFieldDecoder implements FieldDecoder<Value> {
    private final TypeEncodingProvider<byte[]> bytesEncoding;

    @Override
    public Object decodeBytesField(JavaType type, byte[] bytes) {
        return bytesEncoding.encodingFor(type).decode(bytes);
    }

    @Override
    public byte[] decodeBytes(Value value) {
        return value.getBlob().toByteArray();
    }

    @Override
    public short decodeShort(Value value) {
        return (short) decodeLong(value);
    }

    @Override
    public int decodeInteger(Value value) {
        return (int) decodeLong(value);
    }

    @Override
    public long decodeLong(Value value) {
        return value.getInteger();
    }

    @Override
    public float decodeFloat(Value value) {
        return (float) decodeDouble(value);
    }

    @Override
    public double decodeDouble(Value value) {
        return value.getDouble();
    }

    @Override
    public boolean decodeBoolean(Value value) {
        return value.getBoolean();
    }

    @Override
    public byte decodeByte(Value value) {
        return value.getBlob().byteAt(0);
    }

    @Override
    public char decodeCharacter(Value value) {
        return value.getString().charAt(0);
    }

    @Override
    public Date decodeDate(Value value) {
        return value.getDate();
    }

    @Override
    public String decodeString(Value value) {
        return value.getString();
    }

    @Override
    public List<?> decodeList(TypeMapping valueType, Context path, Value value) throws IOException {
        final ImmutableList.Builder<Object> list = ImmutableList.builder();

        int index = 0;

        for (final Value v : value.getList()) {
            list.add(valueType.decode(this, path.push(index++), v));
        }

        return list.build();
    }

    @Override
    public Map<?, ?> decodeMap(TypeMapping keyType, TypeMapping valueType, Context path,
            Value value) throws IOException {
        final ImmutableMap.Builder<String, Object> map = ImmutableMap.builder();

        final Entity entity = value.getEntity();

        for (final Map.Entry<String, Value> e : entity.getProperties().entrySet()) {
            final String k = e.getKey();
            final Object v = valueType.decode(this, path.push(k), e.getValue());
            map.put(k, v);
        }

        return map.build();
    }

    @Override
    public EntityDecoder decodeEntity(Value value) {
        return new DatastoreEntityDecoder(bytesEncoding, value.getEntity());
    }
}
