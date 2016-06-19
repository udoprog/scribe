package eu.toolchain.ogt.jackson.encoding;

import eu.toolchain.ogt.jackson.JsonNode;
import eu.toolchain.ogt.Context;
import eu.toolchain.ogt.Encoder;
import lombok.Data;

@Data
public class DoubleEncoder implements Encoder<JsonNode, Double> {
    @Override
    public JsonNode encode(final Context path, final Double instance) {
        return new JsonNode.FloatJsonNode(instance);
    }

    private static final DoubleEncoder INSTANCE = new DoubleEncoder();

    public static DoubleEncoder get() {
        return INSTANCE;
    }
}
