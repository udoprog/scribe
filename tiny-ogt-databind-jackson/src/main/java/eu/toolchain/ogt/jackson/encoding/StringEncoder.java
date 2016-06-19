package eu.toolchain.ogt.jackson.encoding;

import eu.toolchain.ogt.jackson.JsonNode;
import eu.toolchain.ogt.Context;
import eu.toolchain.ogt.Encoder;
import lombok.Data;

@Data
public class StringEncoder implements Encoder<JsonNode, String> {
    @Override
    public JsonNode encode(final Context path, final String instance) {
        return new JsonNode.StringJsonNode(instance);
    }
}
