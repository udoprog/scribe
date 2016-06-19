package eu.toolchain.ogt.jackson.encoding;

import com.google.common.collect.ImmutableList;
import eu.toolchain.ogt.Context;
import eu.toolchain.ogt.Decoder;
import eu.toolchain.ogt.jackson.JsonNode;
import lombok.Data;

import java.util.List;

@Data
public class ListDecoder<ElementSource> implements Decoder<JsonNode, List<ElementSource>> {
    private final Decoder<JsonNode, ElementSource> value;

    @Override
    public List<ElementSource> decode(final Context path, final JsonNode instance) {
        final List<JsonNode> values = instance.visit(new JsonNode.Visitor<List<JsonNode>>() {
            @Override
            public List<JsonNode> visitList(final JsonNode.ListJsonNode list) {
                return list.getValues();
            }
        });

        final ImmutableList.Builder<ElementSource> result = ImmutableList.builder();

        int index = 0;

        for (final JsonNode value : values) {
            final Context p = path.push(index++);
            result.add(this.value.decode(p, value));
        }

        return result.build();
    }
}
