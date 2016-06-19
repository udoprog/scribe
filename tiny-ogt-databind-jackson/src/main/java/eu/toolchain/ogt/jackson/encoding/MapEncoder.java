package eu.toolchain.ogt.jackson.encoding;

import com.google.common.collect.ImmutableMap;
import eu.toolchain.ogt.jackson.JsonNode;
import eu.toolchain.ogt.Context;
import eu.toolchain.ogt.Encoder;
import lombok.Data;

import java.util.Map;

@Data
public class MapEncoder<ValueSource> implements Encoder<JsonNode, Map<String, ValueSource>> {
    private final Encoder<JsonNode, ValueSource> value;

    @Override
    public JsonNode encode(final Context path, final Map<String, ValueSource> instance) {
        final ImmutableMap.Builder<String, JsonNode> result = ImmutableMap.builder();

        for (final Map.Entry<String, ValueSource> e : instance.entrySet()) {
            final Context p = path.push(e.getKey());
            result.put(e.getKey(), value.encode(p, e.getValue()));
        }

        return new JsonNode.ObjectJsonNode(result.build());
    }
}
