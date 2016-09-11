package eu.toolchain.scribe.jackson.encoding;

import com.fasterxml.jackson.core.JsonGenerator;
import eu.toolchain.scribe.Context;
import eu.toolchain.scribe.StreamEncoder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = true)
public class MapStreamEncoder<ValueSource> extends AbstractStreamEncoder<Map<String, ValueSource>> {
  private final StreamEncoder<JsonGenerator, ValueSource> value;

  @Override
  public void streamEncode(
      final Context path, final Map<String, ValueSource> instance, JsonGenerator target
  ) {
    try {
      target.writeStartObject();
    } catch (final Exception e) {
      throw path.error("failed to write start of object", e);
    }

    for (final Map.Entry<String, ValueSource> entry : instance.entrySet()) {
      final Context p = path.push(entry.getKey());

      value.streamEncodeOptionally(p, entry.getValue(), target, callback -> {
        try {
          target.writeFieldName(entry.getKey());
        } catch (final Exception e) {
          throw p.error("failed to write field name", e);
        }

        callback.run();
      });
    }

    try {
      target.writeEndObject();
    } catch (final Exception e) {
      throw path.error("failed to write end of object", e);
    }
  }
}
