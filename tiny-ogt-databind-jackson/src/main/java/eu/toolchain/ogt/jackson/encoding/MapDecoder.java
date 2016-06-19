package eu.toolchain.ogt.jackson.encoding;

import com.google.common.collect.ImmutableMap;
import eu.toolchain.ogt.jackson.JsonNode;
import eu.toolchain.ogt.Context;
import eu.toolchain.ogt.Decoder;
import lombok.Data;

import java.util.Map;

@Data
public class MapDecoder<ValueSource> implements Decoder<JsonNode, Map<String, ValueSource>> {
    private final Decoder<JsonNode, ValueSource> value;

    @Override
    public Map<String, ValueSource> decode(final Context path, final JsonNode instance) {
        final Map<String, JsonNode> values =
            instance.visit(new JsonNode.Visitor<Map<String, JsonNode>>() {
                @Override
                public Map<String, JsonNode> visitObject(final JsonNode.ObjectJsonNode object) {
                    return object.getFields();
                }
            });

        final ImmutableMap.Builder<String, ValueSource> result = ImmutableMap.builder();

        for (final Map.Entry<String, JsonNode> e : values.entrySet()) {
            final Context p = path.push(e.getKey());
            result.put(e.getKey(), value.decode(p, e.getValue()));
        }

        return result.build();
    }
}
