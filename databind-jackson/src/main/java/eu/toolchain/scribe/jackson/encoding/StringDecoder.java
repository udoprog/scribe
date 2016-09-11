package eu.toolchain.scribe.jackson.encoding;

import eu.toolchain.scribe.Context;
import eu.toolchain.scribe.Decoded;
import eu.toolchain.scribe.Decoder;
import eu.toolchain.scribe.jackson.JsonNode;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class StringDecoder extends AbstractVisitor<String> implements Decoder<JsonNode, String> {
  @Override
  public Decoded<String> visitString(final JsonNode.StringJsonNode string) {
    return Decoded.of(string.getValue());
  }

  @Override
  public Decoded<String> decode(final Context path, final JsonNode instance) {
    return instance.visit(this);
  }

  private static final StringDecoder INSTANCE = new StringDecoder();

  public static StringDecoder get() {
    return INSTANCE;
  }
}
