package eu.toolchain.scribe.jackson.encoding;

import eu.toolchain.scribe.Context;
import eu.toolchain.scribe.Encoder;
import eu.toolchain.scribe.jackson.JsonNode;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class ListEncoder<ElementSource> extends AbstractEncoder<List<ElementSource>> {
  private final Encoder<JsonNode, ElementSource> value;

  @Override
  public JsonNode encode(final Context path, final List<ElementSource> instance) {
    final List<JsonNode> result = new ArrayList<>(instance.size());

    int index = 0;

    for (final ElementSource value : instance) {
      final Context p = path.push(index++);
      this.value.encodeOptionally(p, value, result::add);
    }

    return new JsonNode.ListJsonNode(result);
  }
}
