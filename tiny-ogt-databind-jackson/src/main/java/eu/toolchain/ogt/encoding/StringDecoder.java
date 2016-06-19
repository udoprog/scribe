package eu.toolchain.ogt.encoding;

import eu.toolchain.ogt.Context;
import eu.toolchain.ogt.Decoder;
import eu.toolchain.ogt.JsonNode;
import lombok.Data;

@Data
public class StringDecoder implements Decoder<JsonNode, String> {
    @Override
    public String decode(final Context path, final JsonNode instance) {
        return instance.visit(new JsonNode.Visitor<String>() {
            @Override
            public String visitString(final JsonNode.StringJsonNode string) {
                return string.getValue();
            }
        });
    }
}
