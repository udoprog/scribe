package eu.toolchain.ogt.jackson.encoding;

import eu.toolchain.ogt.jackson.JsonNode;
import eu.toolchain.ogt.Context;
import eu.toolchain.ogt.Encoder;
import lombok.Data;

@Data
public class FloatEncoder implements Encoder<JsonNode, Float> {
    @Override
    public JsonNode encode(final Context path, final Float instance) {
        return new JsonNode.FloatJsonNode(instance);
    }

    private static final FloatEncoder INSTANCE = new FloatEncoder();
    public static FloatEncoder get() {
        return INSTANCE;
    }
}
