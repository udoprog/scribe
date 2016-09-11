package eu.toolchain.scribe.jackson.encoding;

import com.fasterxml.jackson.core.JsonGenerator;
import eu.toolchain.scribe.Context;
import eu.toolchain.scribe.StreamEncoder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class ListStreamEncoder<ElementSource> extends AbstractStreamEncoder<List<ElementSource>> {
  private final StreamEncoder<JsonGenerator, ElementSource> value;

  @Override
  public void streamEncode(
      final Context path, final List<ElementSource> instance, final JsonGenerator target
  ) {
    int index = 0;

    try {
      target.writeStartArray();
    } catch (final Exception e) {
      throw path.error(e);
    }

    for (final ElementSource value : instance) {
      final Context p = path.push(index++);
      this.value.streamEncodeOptionally(p, value, target, Runnable::run);
    }

    try {
      target.writeEndArray();
    } catch (final Exception e) {
      throw path.error(e);
    }
  }
}
