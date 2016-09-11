package eu.toolchain.scribe.datastore.encoding;

import com.google.datastore.v1.Entity;
import com.google.datastore.v1.Value;

import eu.toolchain.scribe.Context;
import eu.toolchain.scribe.Encoder;

import java.util.Map;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class MapEncoder<ValueSource> extends AbstractEncoder<Map<String, ValueSource>> {
  private final Encoder<Value, ValueSource> value;

  @Override
  public Value encode(final Context path, final Map<String, ValueSource> instance) {
    final Entity.Builder entity = Entity.newBuilder();

    for (final Map.Entry<String, ValueSource> e : instance.entrySet()) {
      final Context p = path.push(e.getKey());

      value.encodeOptionally(p, e.getValue(), target -> {
        entity.getMutableProperties().put(e.getKey(), target);
      });
    }

    return Value.newBuilder().setEntityValue(entity.build()).build();
  }
}
