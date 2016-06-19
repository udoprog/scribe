package eu.toolchain.ogt.jackson.encoding;

import eu.toolchain.ogt.jackson.JsonNode;
import eu.toolchain.ogt.Context;
import eu.toolchain.ogt.Decoder;
import lombok.Data;

@Data
public class FloatDecoder implements Decoder<JsonNode, Float> {
    @Override
    public Float decode(final Context path, final JsonNode instance) {
        return instance.visit(new JsonNode.Visitor<Float>() {
            @Override
            public Float visitNumber(final JsonNode.NumberJsonNode numberNode) {
                return (float) numberNode.getValue();
            }

            @Override
            public Float visitFloat(final JsonNode.FloatJsonNode floatNode) {
                return (float) floatNode.getValue();
            }
        });
    }

    private static final FloatDecoder INSTANCE = new FloatDecoder();
    public static FloatDecoder get() {
        return INSTANCE;
    }
}
