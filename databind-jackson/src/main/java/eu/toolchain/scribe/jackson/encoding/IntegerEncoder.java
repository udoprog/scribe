package eu.toolchain.scribe.jackson.encoding;

import eu.toolchain.scribe.Context;
import eu.toolchain.scribe.jackson.JsonNode;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class IntegerEncoder extends AbstractEncoder<Integer> {
  @Override
  public JsonNode encode(final Context path, final Integer instance) {
    return new JsonNode.NumberJsonNode(instance);
  }

  private static final IntegerEncoder INSTANCE = new IntegerEncoder();

  public static IntegerEncoder get() {
    return INSTANCE;
  }
}
