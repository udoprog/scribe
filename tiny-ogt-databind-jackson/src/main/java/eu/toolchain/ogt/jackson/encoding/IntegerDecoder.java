package eu.toolchain.ogt.jackson.encoding;

import eu.toolchain.ogt.jackson.JsonNode;
import eu.toolchain.ogt.Context;
import eu.toolchain.ogt.Decoder;
import lombok.Data;

@Data
public class IntegerDecoder implements Decoder<JsonNode, Integer> {
    @Override
    public Integer decode(final Context path, final JsonNode instance) {
        return instance.visit(new JsonNode.Visitor<Integer>() {
            @Override
            public Integer visitNumber(final JsonNode.NumberJsonNode numberNode) {
                return (int) numberNode.getValue();
            }

            @Override
            public Integer visitFloat(final JsonNode.FloatJsonNode floatNode) {
                return (int) floatNode.getValue();
            }
        });
    }

    private static final IntegerDecoder INSTANCE = new IntegerDecoder();

    public static IntegerDecoder get() {
        return INSTANCE;
    }
}
