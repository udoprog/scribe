package eu.toolchain.ogt.encoding;

import eu.toolchain.ogt.Context;
import eu.toolchain.ogt.Decoder;
import eu.toolchain.ogt.Encoder;
import eu.toolchain.ogt.JsonNode;
import lombok.Data;

@Data
public class DoubleDecoder implements Decoder<JsonNode, Double> {
    @Override
    public Double decode(final Context path, final JsonNode instance) {
        return instance.visit(new JsonNode.Visitor<Double>() {
            @Override
            public Double visitNumber(final JsonNode.NumberJsonNode numberNode) {
                return (double) numberNode.getValue();
            }

            @Override
            public Double visitFloat(final JsonNode.FloatJsonNode floatNode) {
                return floatNode.getValue();
            }
        });
    }

    private static final DoubleDecoder INSTANCE = new DoubleDecoder();
    public static DoubleDecoder get() {
        return INSTANCE;
    }
}
