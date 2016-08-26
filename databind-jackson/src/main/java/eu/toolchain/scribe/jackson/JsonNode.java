package eu.toolchain.scribe.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import lombok.Data;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface JsonNode {
  void generate(final JsonGenerator generator) throws IOException;

  <T> T visit(Visitor<T> visitor);

  static JsonNode fromParser(final JsonParser parser) throws IOException {
    parser.nextToken();
    return currentFromParser(parser);
  }

  static JsonNode currentFromParser(final JsonParser parser) throws IOException {
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
      case VALUE_NULL:
        return NullJsonNode.get();
      default:
        throw new IllegalStateException("Unsupported token: " + token);
    }
  }

  @Data
  class ObjectJsonNode implements JsonNode {
    private final Map<String, JsonNode> fields;

    @Override
    public void generate(final JsonGenerator generator) throws IOException {
      generator.writeStartObject();

      for (final Map.Entry<String, JsonNode> e : fields.entrySet()) {
        generator.writeFieldName(e.getKey());
        e.getValue().generate(generator);
      }

      generator.writeEndObject();
    }

    @Override
    public <T> T visit(final Visitor<T> visitor) {
      return visitor.visitObject(this);
    }

    public static JsonNode fromParser(final JsonParser parser) throws IOException {
      final Map<String, JsonNode> fields = new HashMap<>();

      while (parser.nextToken() != JsonToken.END_OBJECT) {
        final String field = parser.getCurrentName();
        final JsonNode node = JsonNode.fromParser(parser);
        fields.put(field, node);
      }

      return new ObjectJsonNode(fields);
    }
  }

  @Data
  class ListJsonNode implements JsonNode {
    private final List<JsonNode> values;

    @Override
    public void generate(final JsonGenerator generator) throws IOException {
      generator.writeStartArray();

      for (final JsonNode n : values) {
        n.generate(generator);
      }

      generator.writeEndArray();
    }

    @Override
    public <T> T visit(final Visitor<T> visitor) {
      return visitor.visitList(this);
    }

    public static JsonNode fromParser(final JsonParser parser) throws IOException {
      final List<JsonNode> values = new ArrayList<>();

      while (parser.nextToken() != JsonToken.END_ARRAY) {
        values.add(JsonNode.currentFromParser(parser));
      }

      return new ListJsonNode(values);
    }
  }

  @Data
  class StringJsonNode implements JsonNode {
    private final String value;

    @Override
    public void generate(final JsonGenerator generator) throws IOException {
      generator.writeString(value);
    }

    @Override
    public <T> T visit(final Visitor<T> visitor) {
      return visitor.visitString(this);
    }

    public static JsonNode fromParser(final JsonParser parser) throws IOException {
      return new StringJsonNode(parser.getValueAsString());
    }
  }

  @Data
  class BooleanJsonNode implements JsonNode {
    public static final JsonNode TRUE = new BooleanJsonNode(true);
    public static final JsonNode FALSE = new BooleanJsonNode(false);

    private final boolean value;

    public boolean getValue() {
      return value;
    }

    @Override
    public void generate(final JsonGenerator generator) throws IOException {
      generator.writeBoolean(value);
    }

    @Override
    public <T> T visit(final Visitor<T> visitor) {
      return visitor.visitBoolean(this);
    }
  }

  @Data
  class NullJsonNode implements JsonNode {
    @Override
    public void generate(final JsonGenerator generator) throws IOException {
      generator.writeNull();
    }

    @Override
    public <T> T visit(final Visitor<T> visitor) {
      return visitor.visitNull(this);
    }

    private static final NullJsonNode INSTANCE = new NullJsonNode();

    public static NullJsonNode get() {
      return INSTANCE;
    }
  }

  @Data
  class NumberJsonNode implements JsonNode {
    private final long value;

    @Override
    public void generate(final JsonGenerator generator) throws IOException {
      generator.writeNumber(value);
    }

    @Override
    public <T> T visit(final Visitor<T> visitor) {
      return visitor.visitNumber(this);
    }
  }

  @Data
  class FloatJsonNode implements JsonNode {
    private final double value;

    @Override
    public void generate(final JsonGenerator generator) throws IOException {
      generator.writeNumber(value);
    }

    @Override
    public <T> T visit(final Visitor<T> visitor) {
      return visitor.visitFloat(this);
    }
  }

  interface Visitor<T> {
    default T visitDefault(JsonNode node) {
      throw new IllegalArgumentException("Cannot handle type: " + node);
    }

    default T visitNull(NullJsonNode n) {
      return visitDefault(n);
    }

    default T visitObject(ObjectJsonNode object) {
      return visitDefault(object);
    }

    default T visitList(ListJsonNode list) {
      return visitDefault(list);
    }

    default T visitString(StringJsonNode string) {
      return visitDefault(string);
    }

    default T visitBoolean(BooleanJsonNode booleanNode) {
      return visitDefault(booleanNode);
    }

    default T visitNumber(NumberJsonNode numberNode) {
      return visitDefault(numberNode);
    }

    default T visitFloat(FloatJsonNode floatNode) {
      return visitDefault(floatNode);
    }
  }
}
