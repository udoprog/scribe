package eu.toolchain.scribe.datastore.encoding;

import com.google.datastore.v1.ArrayValue;
import com.google.datastore.v1.Value;

import eu.toolchain.scribe.Context;
import eu.toolchain.scribe.Encoder;

import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ListEncoder<ElementSource> extends AbstractEncoder<List<ElementSource>> {
  private final Encoder<Value, ElementSource> value;

  @Override
  public Value encode(final Context path, final List<ElementSource> instance) {
    final ArrayValue.Builder result = ArrayValue.newBuilder();

    int index = 0;

    for (final ElementSource value : instance) {
      final Context p = path.push(index++);
      this.value.encodeOptionally(p, value, result::addValues);
    }

    return Value.newBuilder().setArrayValue(result.build()).build();
  }
}
