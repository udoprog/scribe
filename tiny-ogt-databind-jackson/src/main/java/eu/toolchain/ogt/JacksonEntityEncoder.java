package eu.toolchain.ogt;

import com.google.common.collect.ImmutableMap;

import java.io.IOException;

import eu.toolchain.ogt.binding.FieldMapping;

public class JacksonEntityEncoder implements EntityEncoder {
    final ImmutableMap.Builder<String, JsonNode> object = ImmutableMap.builder();

    @Override
    public void setField(FieldMapping field, Context path, Object value) throws IOException {
        final JsonNode v = (JsonNode) field.type().encode(new JacksonFieldEncoder(), path, value);
        object.put(field.name(), v);
    }

    @Override
    public void setType(String type) throws IOException {
        object.put("type", new JsonNode.StringJsonNode(type));
    }

    @Override
    public Object encode() {
        return new JsonNode.ObjectJsonNode(object.build());
    }
}
