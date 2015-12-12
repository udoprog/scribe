package eu.toolchain.ogt;

import java.util.Optional;

import eu.toolchain.ogt.binding.FieldMapping;

public class JacksonEntityDecoder implements EntityDecoder {
    private final JsonNode node;

    public JacksonEntityDecoder(final JsonNode node) {
        this.node = node;
    }

    @Override
    public void start() {
    }

    @Override
    public void end() {
    }

    @Override
    public Optional<FieldDecoder> getField(FieldMapping field) {
        return node.get(field.name()).map(JacksonFieldDecoder::new);
    }

    @Override
    public Optional<String> getType() {
        return node.get("type").map(JsonNode::asString);
    }
}
