package eu.toolchain.scribe.jackson.encoding;

import eu.toolchain.scribe.Context;
import eu.toolchain.scribe.jackson.JsonNode;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ShortEncoder extends AbstractEncoder<Short> {
  @Override
  public JsonNode encode(final Context path, final Short instance) {
    return new JsonNode.NumberJsonNode(instance);
  }

  private static final ShortEncoder INSTANCE = new ShortEncoder();

  public static ShortEncoder get() {
    return INSTANCE;
  }
}
