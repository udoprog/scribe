package eu.toolchain.ogt.encoding;

import eu.toolchain.ogt.Context;
import eu.toolchain.ogt.Encoder;
import eu.toolchain.ogt.JsonNode;
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
