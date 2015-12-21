package eu.toolchain.ogt;

import java.util.Optional;

import eu.toolchain.ogt.binding.FieldMapping;

public class JacksonEntityDecoder implements EntityDecoder {
    private final JsonNode node;

    public JacksonEntityDecoder(final JsonNode node) {
        this.node = node;
    }

    @Override
    public Optional<String> decodeType() {
        return node.get("type").map(JsonNode::asString);
    }

    @Override
    public Optional<Object> decodeField(FieldMapping field, Context path) {
        return node.get(field.name())
                .map(n -> field.type().decode(new JacksonFieldDecoder(n), path));
    }
}
