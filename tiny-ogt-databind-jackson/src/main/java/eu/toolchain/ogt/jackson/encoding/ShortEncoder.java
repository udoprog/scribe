package eu.toolchain.ogt.jackson.encoding;

import eu.toolchain.ogt.Context;
import eu.toolchain.ogt.Encoder;
import eu.toolchain.ogt.jackson.JsonNode;
import lombok.Data;

@Data
public class ShortEncoder implements Encoder<JsonNode, Short> {
    @Override
    public JsonNode encode(final Context path, final Short instance) {
        return new JsonNode.NumberJsonNode(instance);
    }

    private static final ShortEncoder INSTANCE = new ShortEncoder();

    public static ShortEncoder get() {
        return INSTANCE;
    }
}
