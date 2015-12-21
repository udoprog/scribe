package eu.toolchain.ogt;

import com.google.common.collect.ImmutableList;
import com.google.protobuf.ByteString;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import eu.toolchain.ogt.binding.FieldMapping;
import eu.toolchain.ogt.type.TypeMapping;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DatastoreFieldEncoder implements FieldEncoder {
    private final TypeEncodingProvider<byte[]> bytesEncoding;

    @Override
    public byte[] encode(JavaType type, Object value) {
        return bytesEncoding.encodingFor(type).encode(value);
    }

    @Override
    public Object encodeBytes(byte[] bytes) throws IOException {
        return ByteString.copyFrom(bytes);
    }

    @Override
    public Object encodeShort(short value) throws IOException {
        return value;
    }

    @Override
    public Object encodeInteger(int value) throws IOException {
        return value;
    }

    @Override
    public Object encodeLong(long value) throws IOException {
        return value;
    }

    @Override
    public Object encodeFloat(float value) throws IOException {
        return value;
    }

    @Override
    public Object encodeDouble(double value) throws IOException {
        return value;
    }

    @Override
    public Object encodeBoolean(boolean value) throws IOException {
        return value;
    }

    @Override
    public Object encodeByte(byte value) throws IOException {
        return new byte[] {value};
    }

    @Override
    public Object encodeCharacter(char value) throws IOException {
        return new String(new char[] {value});
    }

    @Override
    public Object encodeDate(Date value) throws IOException {
        return value;
    }

    @Override
    public Object encodeString(String value) throws IOException {
        return value;
    }

    @Override
    public Object encodeList(TypeMapping value, List<?> list, Context path) throws IOException {
        final ImmutableList.Builder<Object> values = ImmutableList.builder();

        int index = 0;

        for (final Object v : list) {
            values.add(value.encode(this, path.push(index++), v));
        }

        return values.build();
    }

    @Override
    public Object encodeMap(TypeMapping key, TypeMapping value, Map<?, ?> map, Context path)
            throws IOException {
        if (!key.getType().getRawClass().equals(String.class)) {
            throw path.error("Keys must be strings");
        }

        @SuppressWarnings("unchecked")
        final Map<String, ?> stringMap = (Map<String, ?>) map;

        final DatastoreEntityEncoder encoder = new DatastoreEntityEncoder(bytesEncoding);

        for (final Map.Entry<String, ?> e : stringMap.entrySet()) {
            final FieldMapping field = new MapFieldMapping(e.getKey(), value);
            encoder.setField(field, path.push(field.name()), e.getValue());
        }

        return encoder.encode();
    }

    @Override
    public EntityEncoder encodeEntity() {
        return new DatastoreEntityEncoder(bytesEncoding);
    }

    @RequiredArgsConstructor
    public static class MapFieldMapping implements FieldMapping {
        private final String name;
        private final TypeMapping type;

        @Override
        public String name() {
            return name;
        }

        @Override
        public boolean indexed() {
            return false;
        }

        @Override
        public TypeMapping type() {
            return type;
        }
    }
}
