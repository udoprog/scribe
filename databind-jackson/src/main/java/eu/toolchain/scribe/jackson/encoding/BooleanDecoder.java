package eu.toolchain.scribe.jackson.encoding;

import eu.toolchain.scribe.Context;
import eu.toolchain.scribe.Decoded;
import eu.toolchain.scribe.Decoder;
import eu.toolchain.scribe.jackson.JsonNode;
import lombok.Data;

@Data
public class BooleanDecoder extends AbstractVisitor<Boolean> implements Decoder<JsonNode, Boolean> {
  @Override
  public Decoded<Boolean> visitBoolean(final JsonNode.BooleanJsonNode booleanNode) {
    return Decoded.of(booleanNode.getValue());
  }

  @Override
  public Decoded<Boolean> decode(final Context path, final JsonNode instance) {
    return instance.visit(this);
  }

  private static final BooleanDecoder INSTANCE = new BooleanDecoder();

  public static BooleanDecoder get() {
    return INSTANCE;
  }
}
