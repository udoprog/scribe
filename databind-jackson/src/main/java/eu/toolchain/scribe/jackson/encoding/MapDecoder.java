package eu.toolchain.scribe.jackson.encoding;

import eu.toolchain.scribe.Context;
import eu.toolchain.scribe.Decoded;
import eu.toolchain.scribe.Decoder;
import eu.toolchain.scribe.jackson.JsonNode;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.HashMap;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = true)
public class MapDecoder<ValueSource> extends AbstractVisitor<Map<String, JsonNode>>
    implements Decoder<JsonNode, Map<String, ValueSource>> {
  @Override
  public Decoded<Map<String, JsonNode>> visitObject(
      final JsonNode.ObjectJsonNode object
  ) {
    return Decoded.of(object.getFields());
  }

  private final Decoder<JsonNode, ValueSource> value;

  @Override
  public Decoded<Map<String, ValueSource>> decode(
      final Context path, final JsonNode instance
  ) {
    return instance.visit(this).map(values -> {
      final Map<String, ValueSource> result = new HashMap<>(values.size());

      for (final Map.Entry<String, JsonNode> e : values.entrySet()) {
        final Context p = path.push(e.getKey());
        value.decode(p, e.getValue()).ifPresent(v -> result.put(e.getKey(), v));
      }

      return result;
    });
  }
}
