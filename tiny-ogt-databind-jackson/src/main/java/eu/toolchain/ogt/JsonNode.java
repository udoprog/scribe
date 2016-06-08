package eu.toolchain.ogt;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.BaseEncoding;
import lombok.Data;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface JsonNode {
    default Optional<JsonNode> get(final String field) {
        throw new RuntimeException(this + ": not an object");
    }

    default List<JsonNode> asList() {
        throw new RuntimeException(this + ": not a list");
    }

    default Map<String, JsonNode> asObject() {
        throw new RuntimeException(this + ": not an object");
    }

    default byte[] asBytes() {
        throw new RuntimeException(this + ": not a string");
    }

    default String asString() {
        throw new RuntimeException(this + ": not a string");
    }

    default boolean asBoolean() {
        throw new RuntimeException(this + ": not a boolean");
    }

    default double asDouble() {
        throw new RuntimeException(this + ": not a number");
    }

    default float asFloat() {
        throw new RuntimeException(this + ": not a number");
    }

    default byte asByte() {
        throw new RuntimeException(this + ": not a number");
    }

    default short asShort() {
        throw new RuntimeException(this + ": not a number");
    }

    default int asInteger() {
        throw new RuntimeException(this + ": not a number");
    }

    default long asLong() {
        throw new RuntimeException(this + ": not a number");
    }

    default char asCharacter() {
        throw new RuntimeException(this + ": not a string");
    }

    void generate(final JsonGenerator generator) throws IOException;

    public static JsonNode fromParser(final JsonParser parser) throws IOException {
        parser.nextToken();
        return currentFromParser(parser);
    }

    public static JsonNode currentFromParser(final JsonParser parser) throws IOException {
        final JsonToken token = parser.getCurrentToken();

        if (token == null) {
            throw new IllegalStateException("Current token is null");
        }

        switch (token) {
            case START_OBJECT:
                return ObjectJsonNode.fromParser(parser);
            case START_ARRAY:
                return ListJsonNode.fromParser(parser);
            case VALUE_STRING:
                return StringJsonNode.fromParser(parser);
            case VALUE_FALSE:
                return BooleanJsonNode.FALSE;
            case VALUE_TRUE:
                return BooleanJsonNode.TRUE;
            case VALUE_NUMBER_FLOAT:
                return new FloatJsonNode(parser.getDoubleValue());
            case VALUE_NUMBER_INT:
                return new NumberJsonNode(parser.getLongValue());
            default:
                throw new IllegalStateException("Unsupported token: " + token);
        }
    }

    @Data
    public static class ObjectJsonNode implements JsonNode {
        private final Map<String, JsonNode> fields;

        @Override
        public Map<String, JsonNode> asObject() {
            return fields;
        }

        @Override
        public Optional<JsonNode> get(String field) {
            return Optional.ofNullable(fields.get(field));
        }

        @Override
        public void generate(final JsonGenerator generator) throws IOException {
            generator.writeStartObject();

            for (final Map.Entry<String, JsonNode> e : fields.entrySet()) {
                generator.writeFieldName(e.getKey());
                e.getValue().generate(generator);
            }

            generator.writeEndObject();
        }

        public static JsonNode fromParser(final JsonParser parser) throws IOException {
            final ImmutableMap.Builder<String, JsonNode> fields = ImmutableMap.builder();

            while (parser.nextToken() != JsonToken.END_OBJECT) {
                final String field = parser.getCurrentName();
                final JsonNode node = JsonNode.fromParser(parser);
                fields.put(field, node);
            }

            return new ObjectJsonNode(fields.build());
        }
    }

    @Data
    public static class ListJsonNode implements JsonNode {
        private final List<JsonNode> values;

        @Override
        public List<JsonNode> asList() {
            return values;
        }

        public static JsonNode fromParser(final JsonParser parser) throws IOException {
            final ImmutableList.Builder<JsonNode> values = ImmutableList.builder();

            while (parser.nextToken() != JsonToken.END_ARRAY) {
                values.add(JsonNode.currentFromParser(parser));
            }

            return new ListJsonNode(values.build());
        }

        @Override
        public void generate(final JsonGenerator generator) throws IOException {
            generator.writeStartArray();

            for (final JsonNode n : values) {
                n.generate(generator);
            }

            generator.writeEndArray();
        }
    }

    @Data
    public static class StringJsonNode implements JsonNode {
        public static final BaseEncoding BASE64 = BaseEncoding.base64();

        private final String value;

        @Override
        public byte[] asBytes() {
            return BASE64.decode(value);
        }

        @Override
        public String asString() {
            return value;
        }

        @Override
        public char asCharacter() {
            return value.charAt(0);
        }

        @Override
        public void generate(final JsonGenerator generator) throws IOException {
            generator.writeString(value);
        }

        public static JsonNode fromParser(final JsonParser parser) throws IOException {
            return new StringJsonNode(parser.getValueAsString());
        }
    }

    @Data
    public static class BooleanJsonNode implements JsonNode {
        public static final JsonNode TRUE = new BooleanJsonNode(true);
        public static final JsonNode FALSE = new BooleanJsonNode(false);

        private final boolean value;

        @Override
        public boolean asBoolean() {
            return value;
        }

        @Override
        public void generate(final JsonGenerator generator) throws IOException {
            generator.writeBoolean(value);
        }
    }

    @Data
    public static class NumberJsonNode implements JsonNode {
        private final long value;

        @Override
        public byte asByte() {
            return (byte) value;
        }

        @Override
        public short asShort() {
            return (short) value;
        }

        @Override
        public int asInteger() {
            return (int) value;
        }

        @Override
        public long asLong() {
            return value;
        }

        @Override
        public float asFloat() {
            return (float) value;
        }

        @Override
        public double asDouble() {
            return value;
        }

        @Override
        public void generate(final JsonGenerator generator) throws IOException {
            generator.writeNumber(value);
        }
    }

    @Data
    public static class FloatJsonNode implements JsonNode {
        private final double value;

        @Override
        public byte asByte() {
            return (byte) value;
        }

        @Override
        public short asShort() {
            return (short) value;
        }

        @Override
        public int asInteger() {
            return (int) value;
        }

        @Override
        public long asLong() {
            return (long) value;
        }

        @Override
        public float asFloat() {
            return (float) value;
        }

        @Override
        public double asDouble() {
            return value;
        }

        @Override
        public void generate(final JsonGenerator generator) throws IOException {
            generator.writeNumber(value);
        }
    }
}
