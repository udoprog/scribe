package eu.toolchain.scribe.typesafe.encoding;

import com.typesafe.config.ConfigList;
import com.typesafe.config.ConfigValue;
import eu.toolchain.scribe.Context;
import eu.toolchain.scribe.Decoded;
import eu.toolchain.scribe.Decoder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ListDecoder<ElementSource> implements Decoder<ConfigValue, List<ElementSource>> {
  private final Decoder<ConfigValue, ElementSource> value;

  @Override
  public Decoded<List<ElementSource>> decode(
      final Context path, final ConfigValue instance
  ) {
    final List<ConfigValue> values;

    switch (instance.valueType()) {
      case LIST:
        values = ((ConfigList) instance);
        break;
      case NULL:
        return Decoded.absent();
      default:
        throw new IllegalArgumentException("Expected list: " + instance);
    }

    final List<ElementSource> result = new ArrayList<>();

    int index = 0;

    for (final ConfigValue value : values) {
      final Context p = path.push(index++);
      this.value.decode(p, value).ifPresent(result::add);
    }

    return Decoded.of(result);
  }
}
