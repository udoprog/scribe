package eu.toolchain.scribe.datastore.encoding;

import com.google.datastore.v1.Value;

import eu.toolchain.scribe.Context;
import eu.toolchain.scribe.Decoded;
import eu.toolchain.scribe.Decoder;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class ListDecoder<ElementSource>
    implements Decoder<Value, List<ElementSource>> {
  private final Decoder<Value, ElementSource> value;

  @Override
  public Decoded<List<ElementSource>> decode(final Context path, final Value instance) {
    switch (instance.getValueTypeCase()) {
      case ARRAY_VALUE:
        return Decoded.of(decodeList(path, instance.getArrayValue().getValuesList()));
      case NULL_VALUE:
        return Decoded.absent();
      default:
        throw path.error("expected list");
    }
  }

  private List<ElementSource> decodeList(final Context path, final List<Value> values) {
    final List<ElementSource> result = new ArrayList<>(values.size());

    int index = 0;

    for (final Value v : values) {
      final Context p = path.push(index++);
      value.decode(p, v).ifPresent(result::add);
    }

    return result;
  }
}
