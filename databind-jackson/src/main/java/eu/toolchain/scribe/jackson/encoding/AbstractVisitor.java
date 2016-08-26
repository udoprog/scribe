package eu.toolchain.scribe.jackson.encoding;

import eu.toolchain.scribe.Decoded;
import eu.toolchain.scribe.jackson.JsonNode;

public abstract class AbstractVisitor<T> implements JsonNode.Visitor<Decoded<T>> {
  @Override
  public Decoded<T> visitNull(final JsonNode.NullJsonNode n) {
    return Decoded.absent();
  }
}
