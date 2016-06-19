package eu.toolchain.ogt;

import com.google.common.collect.ImmutableMap;
import eu.toolchain.ogt.entitybinding.EntityFieldEncoder;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JacksonEntityEncoder implements EntityEncoder<JsonNode> {
    final ImmutableMap.Builder<String, JsonNode> object = ImmutableMap.builder();

    @Override
    public void setField(
        EntityFieldEncoder<JsonNode, Object> field, Context path, Object value
    ) {
        object.put(field.getName(), field.encode(path, value));
    }

    @Override
    public void setType(String type) {
        object.put("type", new JsonNode.StringJsonNode(type));
    }

    @Override
    public JsonNode build() {
        return new JsonNode.ObjectJsonNode(object.build());
    }
}
