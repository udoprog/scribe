package eu.toolchain.scribe.jackson.encoding;

import eu.toolchain.scribe.Context;
import eu.toolchain.scribe.jackson.JsonNode;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class BooleanEncoder extends AbstractEncoder<Boolean> {
  @Override
  public JsonNode encode(final Context path, final Boolean instance) {
    return new JsonNode.BooleanJsonNode(instance);
  }

  private static final BooleanEncoder INSTANCE = new BooleanEncoder();

  public static BooleanEncoder get() {
    return INSTANCE;
  }
}
