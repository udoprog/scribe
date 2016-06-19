package eu.toolchain.ogt.encoding;

import com.google.common.collect.ImmutableList;
import eu.toolchain.ogt.Context;
import eu.toolchain.ogt.Encoder;
import eu.toolchain.ogt.JsonNode;
import lombok.Data;

import java.util.List;

@Data
public class ListEncoder<ElementSource> implements Encoder<JsonNode, List<ElementSource>> {
    private final Encoder<JsonNode, ElementSource> value;

    @Override
    public JsonNode encode(final Context path, final List<ElementSource> instance) {
        final ImmutableList.Builder<JsonNode> result = ImmutableList.builder();

        int index = 0;

        for (final ElementSource value : instance) {
            final Context p = path.push(index++);
            result.add(this.value.encode(p, value));
        }

        return new JsonNode.ListJsonNode(result.build());
    }
}
