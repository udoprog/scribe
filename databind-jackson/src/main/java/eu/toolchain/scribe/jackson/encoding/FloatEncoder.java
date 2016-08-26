package eu.toolchain.scribe.jackson.encoding;

import eu.toolchain.scribe.Context;
import eu.toolchain.scribe.jackson.JsonNode;
import lombok.Data;

@Data
public class FloatEncoder extends AbstractEncoder<Float> {
  @Override
  public JsonNode encode(final Context path, final Float instance) {
    return new JsonNode.FloatJsonNode(instance);
  }

  private static final FloatEncoder INSTANCE = new FloatEncoder();

  public static FloatEncoder get() {
    return INSTANCE;
  }
}
