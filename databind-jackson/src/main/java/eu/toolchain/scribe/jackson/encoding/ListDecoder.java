package eu.toolchain.scribe.jackson.encoding;

import eu.toolchain.scribe.Context;
import eu.toolchain.scribe.Decoded;
import eu.toolchain.scribe.Decoder;
import eu.toolchain.scribe.jackson.JsonNode;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class ListDecoder<ElementSource> extends AbstractVisitor<List<JsonNode>>
    implements Decoder<JsonNode, List<ElementSource>> {
  @Override
  public Decoded<List<JsonNode>> visitList(final JsonNode.ListJsonNode list) {
    return Decoded.of(list.getValues());
  }

  private final Decoder<JsonNode, ElementSource> value;

  @Override
  public Decoded<List<ElementSource>> decode(final Context path, final JsonNode instance) {
    return instance.visit(this).map(values -> {
      final List<ElementSource> result = new ArrayList<>(values.size());

      int index = 0;

      for (final JsonNode v : values) {
        final Context p = path.push(index++);
        value.decode(p, v).ifPresent(result::add);
      }

      return result;
    });
  }
}
