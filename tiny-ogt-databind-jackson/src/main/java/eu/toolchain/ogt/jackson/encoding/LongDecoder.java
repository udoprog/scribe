package eu.toolchain.ogt.jackson.encoding;

import eu.toolchain.ogt.jackson.JsonNode;
import eu.toolchain.ogt.Context;
import eu.toolchain.ogt.Decoder;
import lombok.Data;

@Data
public class LongDecoder implements Decoder<JsonNode, Long> {
    @Override
    public Long decode(final Context path, final JsonNode instance) {
        return instance.visit(new JsonNode.Visitor<Long>() {
            @Override
            public Long visitNumber(final JsonNode.NumberJsonNode numberNode) {
                return numberNode.getValue();
            }

            @Override
            public Long visitFloat(final JsonNode.FloatJsonNode floatNode) {
                return (long) floatNode.getValue();
            }
        });
    }

    private static final LongDecoder INSTANCE = new LongDecoder();

    public static LongDecoder get() {
        return INSTANCE;
    }
}
