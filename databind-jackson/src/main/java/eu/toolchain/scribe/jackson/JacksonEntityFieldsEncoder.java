package eu.toolchain.scribe.jackson;

import eu.toolchain.scribe.Context;
import eu.toolchain.scribe.EntityFieldsEncoder;
import eu.toolchain.scribe.entitymapping.EntityFieldEncoder;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class JacksonEntityFieldsEncoder implements EntityFieldsEncoder<JsonNode> {
  final Map<String, JsonNode> object = new HashMap<>();

  @Override
  public <Source> void encodeField(
      EntityFieldEncoder<JsonNode, Source> field, Context path, Source value
  ) {
    object.put(field.getName(), field.encode(path, value));
  }

  @Override
  public JsonNode buildEmpty(final Context path) {
    return JsonNode.NullJsonNode.get();
  }

  @Override
  public JsonNode build() {
    return new JsonNode.ObjectJsonNode(object);
  }
}
