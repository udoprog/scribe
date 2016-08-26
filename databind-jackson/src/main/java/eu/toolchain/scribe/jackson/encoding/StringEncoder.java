package eu.toolchain.scribe.jackson.encoding;

import eu.toolchain.scribe.Context;
import eu.toolchain.scribe.jackson.JsonNode;
import lombok.Data;

@Data
public class StringEncoder extends AbstractEncoder<String> {
  @Override
  public JsonNode encode(final Context path, final String instance) {
    return new JsonNode.StringJsonNode(instance);
  }

  private static final StringEncoder INSTANCE = new StringEncoder();

  public static StringEncoder get() {
    return INSTANCE;
  }
}
