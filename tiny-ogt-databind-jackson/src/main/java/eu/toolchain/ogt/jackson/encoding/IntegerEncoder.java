package eu.toolchain.ogt.jackson.encoding;

import eu.toolchain.ogt.Context;
import eu.toolchain.ogt.Encoder;
import eu.toolchain.ogt.jackson.JsonNode;
import lombok.Data;

@Data
public class IntegerEncoder implements Encoder<JsonNode, Integer> {
    @Override
    public JsonNode encode(final Context path, final Integer instance) {
        return new JsonNode.NumberJsonNode(instance);
    }

    private static final IntegerEncoder INSTANCE = new IntegerEncoder();

    public static IntegerEncoder get() {
        return INSTANCE;
    }
}
