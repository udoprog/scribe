package eu.toolchain.scribe.datastore.encoding;

import com.google.datastore.v1.Value;

import eu.toolchain.scribe.Context;
import eu.toolchain.scribe.Decoded;
import eu.toolchain.scribe.Decoder;

import java.util.HashMap;
import java.util.Map;

import lombok.Data;

@Data
public class MapDecoder<ValueSource>
    implements Decoder<Value, Map<String, ValueSource>> {
  private final Decoder<Value, ValueSource> value;

  @Override
  public Decoded<Map<String, ValueSource>> decode(
      final Context path, final Value instance
  ) {
    switch (instance.getValueTypeCase()) {
      case ENTITY_VALUE:
        return Decoded.of(decodeMap(path, instance.getEntityValue().getProperties()));
      case NULL_VALUE:
        return Decoded.absent();
      default:
        throw path.error("expected entity");
    }
  }

  private Map<String, ValueSource> decodeMap(final Context path, final Map<String, Value> values) {
    final Map<String, ValueSource> result = new HashMap<>(values.size());

    for (final Map.Entry<String, Value> e : values.entrySet()) {
      final Context p = path.push(e.getKey());
      value.decode(p, e.getValue()).ifPresent(v -> result.put(e.getKey(), v));
    }

    return result;
  }
}
