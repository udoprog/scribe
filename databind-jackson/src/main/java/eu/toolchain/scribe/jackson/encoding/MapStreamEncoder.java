package eu.toolchain.scribe.jackson.encoding;

import com.fasterxml.jackson.core.JsonGenerator;
import eu.toolchain.scribe.Context;
import eu.toolchain.scribe.StreamEncoder;
import lombok.Data;

import java.io.IOException;
import java.util.Map;

@Data
public class MapStreamEncoder<ValueSource> extends AbstractStreamEncoder<Map<String, ValueSource>> {
  private final StreamEncoder<JsonGenerator, ValueSource> value;

  @Override
  public void streamEncode(
      final Context path, final Map<String, ValueSource> instance, JsonGenerator target
  ) {
    try {
      target.writeStartObject();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    for (final Map.Entry<String, ValueSource> entry : instance.entrySet()) {
      final Context p = path.push(entry.getKey());

      value.streamEncodeOptionally(p, entry.getValue(), target, callback -> {
        try {
          target.writeFieldName(entry.getKey());
        } catch (final IOException e) {
          throw new RuntimeException(e);
        }

        callback.run();
      });
    }

    try {
      target.writeEndObject();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
