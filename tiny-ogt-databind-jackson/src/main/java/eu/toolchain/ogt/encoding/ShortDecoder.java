package eu.toolchain.ogt.encoding;

import eu.toolchain.ogt.Context;
import eu.toolchain.ogt.Decoder;
import eu.toolchain.ogt.JsonNode;
import lombok.Data;

@Data
public class ShortDecoder implements Decoder<JsonNode, Short> {
    @Override
    public Short decode(final Context path, final JsonNode instance) {
        return instance.visit(new JsonNode.Visitor<Short>() {
            @Override
            public Short visitNumber(final JsonNode.NumberJsonNode numberNode) {
                return (short) numberNode.getValue();
            }

            @Override
            public Short visitFloat(final JsonNode.FloatJsonNode floatNode) {
                return (short) floatNode.getValue();
            }
        });
    }

    private static final ShortDecoder INSTANCE = new ShortDecoder();

    public static ShortDecoder get() {
        return INSTANCE;
    }
}
