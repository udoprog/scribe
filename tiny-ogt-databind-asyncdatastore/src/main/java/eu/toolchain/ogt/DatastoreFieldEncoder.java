package eu.toolchain.ogt;

import com.google.common.collect.ImmutableList;
import com.spotify.asyncdatastoreclient.Entity;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import eu.toolchain.ogt.binding.FieldMapping;
import eu.toolchain.ogt.type.TypeMapping;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DatastoreFieldEncoder implements FieldEncoder {
    private final Consumer<Object> consumer;
    private final Consumer<List<Object>> valuesConsumer;

    @Override
    public byte[] encode(JavaType type, Object value) {
        throw new RuntimeException("Not supported");
    }

    @Override
    public void setBytes(byte[] bytes) throws IOException {
        consumer.accept(bytes);
    }

    @Override
    public void setShort(short value) throws IOException {
        consumer.accept(value);
    }

    @Override
    public void setInteger(int value) throws IOException {
        consumer.accept(value);
    }

    @Override
    public void setLong(long value) throws IOException {
        consumer.accept(value);
    }

    @Override
    public void setFloat(float value) throws IOException {
        consumer.accept(value);
    }

    @Override
    public void setDouble(double value) throws IOException {
        consumer.accept(value);
    }

    @Override
    public void setBoolean(boolean value) throws IOException {
        consumer.accept(value);
    }

    @Override
    public void setByte(byte value) throws IOException {
        consumer.accept(value);
    }

    @Override
    public void setCharacter(char value) throws IOException {
        consumer.accept(value);
    }

    @Override
    public void setDate(Date value) throws IOException {
        consumer.accept(value);
    }

    @Override
    public void setString(String string) throws IOException {
        consumer.accept(string);
    }

    @Override
    public void setList(TypeMapping value, List<?> list, Context path) throws IOException {
        final ImmutableList.Builder<Object> values = ImmutableList.builder();

        final Consumer<List<Object>> valuesConsumer = v -> {
            throw new RuntimeException("Lists of lists are not supported");
        };

        final DatastoreFieldEncoder encoder =
                new DatastoreFieldEncoder(v -> values.add(v), valuesConsumer);

        int index = 0;

        for (final Object v : list) {
            value.encode(encoder, v, path.push(index++));
        }

        this.valuesConsumer.accept(values.build());
    }

    @Override
    public void setMap(TypeMapping key, TypeMapping value, Map<?, ?> map, Context path)
            throws IOException {
        if (!key.getType().getRawClass().equals(String.class)) {
            throw path.error("Keys must be strings");
        }

        final Entity.Builder embedded = Entity.builder();

        @SuppressWarnings("unchecked")
        final Map<String, ?> stringMap = (Map<String, ?>) map;

        final DatastoreEntityEncoder encoder = new DatastoreEntityEncoder(embedded);

        for (final Map.Entry<String, ?> e : stringMap.entrySet()) {
            final FieldMapping field = new MapFieldMapping(e.getKey());
            value.encode(encoder.setField(field), e.getValue(), path.push(field.name()));
        }

        consumer.accept(embedded.build());
    }

    @Override
    public EntityEncoder setEntity() {
        final Entity.Builder builder = Entity.builder();

        return new DatastoreEntityEncoder(builder) {
            @Override
            public void endEntity() {
                consumer.accept(builder.build());
            }
        };
    }

    @RequiredArgsConstructor
    public static class MapFieldMapping implements FieldMapping {
        private final String name;

        @Override
        public String name() {
            return name;
        }

        @Override
        public boolean indexed() {
            return false;
        }
    }
}
