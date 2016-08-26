package eu.toolchain.scribe.jackson.encoding;

import eu.toolchain.scribe.Context;
import eu.toolchain.scribe.Encoder;
import eu.toolchain.scribe.jackson.JsonNode;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class MapEncoder<ValueSource> extends AbstractEncoder<Map<String, ValueSource>> {
  private final Encoder<JsonNode, ValueSource> value;

  @Override
  public JsonNode encode(final Context path, final Map<String, ValueSource> instance) {
    final Map<String, JsonNode> result = new HashMap<>(instance.size());

    for (final Map.Entry<String, ValueSource> e : instance.entrySet()) {
      final Context p = path.push(e.getKey());

      value.encodeOptionally(p, e.getValue(), target -> {
        result.put(e.getKey(), target);
      });
    }

    return new JsonNode.ObjectJsonNode(result);
  }
}
