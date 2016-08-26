package eu.toolchain.scribe.jackson.encoding;

import eu.toolchain.scribe.Context;
import eu.toolchain.scribe.jackson.JsonNode;
import lombok.Data;

@Data
public class LongEncoder extends AbstractEncoder<Long> {
  @Override
  public JsonNode encode(final Context path, final Long instance) {
    return new JsonNode.NumberJsonNode(instance);
  }

  private static final LongEncoder INSTANCE = new LongEncoder();

  public static LongEncoder get() {
    return INSTANCE;
  }
}
