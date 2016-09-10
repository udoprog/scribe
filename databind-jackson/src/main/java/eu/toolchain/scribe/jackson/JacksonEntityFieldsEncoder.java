package eu.toolchain.scribe.jackson;

import eu.toolchain.scribe.Context;
import eu.toolchain.scribe.EntityFieldEncoder;
import eu.toolchain.scribe.EntityFieldsEncoder;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class JacksonEntityFieldsEncoder
    implements EntityFieldsEncoder<JsonNode, JsonNode.ObjectJsonNode> {
  final Map<String, JsonNode> object = new HashMap<>();

  @Override
  public <Source> void encodeField(
      EntityFieldEncoder<JsonNode, Source> field, Context path, Source value
  ) {
    field.encodeOptionally(path.push(field.getName()), value,
        target -> object.put(field.getName(), target));
  }

  @Override
  public JsonNode.ObjectJsonNode buildEmpty(final Context path) {
    return JsonNode.ObjectJsonNode.empty();
  }

  @Override
  public JsonNode.ObjectJsonNode build() {
    return new JsonNode.ObjectJsonNode(object);
  }
}
