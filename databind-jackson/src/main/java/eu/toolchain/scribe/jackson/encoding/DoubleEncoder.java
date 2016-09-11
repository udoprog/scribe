package eu.toolchain.scribe.jackson.encoding;

import eu.toolchain.scribe.Context;
import eu.toolchain.scribe.jackson.JsonNode;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class DoubleEncoder extends AbstractEncoder<Double> {
  @Override
  public JsonNode encode(final Context path, final Double instance) {
    return new JsonNode.FloatJsonNode(instance);
  }

  private static final DoubleEncoder INSTANCE = new DoubleEncoder();

  public static DoubleEncoder get() {
    return INSTANCE;
  }
}
