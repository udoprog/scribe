package eu.toolchain.ogt;

import eu.toolchain.ogt.binding.FieldMapping;

import java.util.Optional;

public class JacksonEntityDecoder implements EntityDecoder<JsonNode> {
    @Override
    public Optional<String> decodeType(JsonNode node) {
        return node.get("type").map(JsonNode::asString);
    }

    @Override
    public Optional<Object> decodeField(FieldMapping field, Context path, JsonNode node) {
        return node
            .get(field.name())
            .map(n -> field.type().decode(new JacksonFieldDecoder(), path, n));
    }
}
