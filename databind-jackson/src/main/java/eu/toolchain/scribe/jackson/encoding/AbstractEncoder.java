package eu.toolchain.scribe.jackson.encoding;

import eu.toolchain.scribe.Context;
import eu.toolchain.scribe.Encoder;
import eu.toolchain.scribe.jackson.JsonNode;

public abstract class AbstractEncoder<T> implements Encoder<JsonNode, T> {
  @Override
  public JsonNode encodeEmpty(final Context path) {
    return JsonNode.NullJsonNode.get();
  }
}
