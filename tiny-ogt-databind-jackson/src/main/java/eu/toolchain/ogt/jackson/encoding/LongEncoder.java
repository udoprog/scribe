package eu.toolchain.ogt.jackson.encoding;

import eu.toolchain.ogt.jackson.JsonNode;
import eu.toolchain.ogt.Context;
import eu.toolchain.ogt.Encoder;
import lombok.Data;

@Data
public class LongEncoder implements Encoder<JsonNode, Long> {
    @Override
    public JsonNode encode(final Context path, final Long instance) {
        return new JsonNode.NumberJsonNode(instance);
    }

    private static final LongEncoder INSTANCE = new LongEncoder();

    public static LongEncoder get() {
        return INSTANCE;
    }
}
