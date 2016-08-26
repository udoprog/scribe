package eu.toolchain.scribe.typesafe.encoding;

import com.typesafe.config.ConfigValue;
import com.typesafe.config.ConfigValueFactory;
import eu.toolchain.scribe.Context;
import eu.toolchain.scribe.Encoder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ListEncoder<ElementSource> extends AbstractEncoder<List<ElementSource>> {
  private final Encoder<ConfigValue, ElementSource> value;

  @Override
  public ConfigValue encode(final Context path, final List<ElementSource> instance) {
    final List<ConfigValue> result = new ArrayList<>();

    int index = 0;

    for (final ElementSource value : instance) {
      final Context p = path.push(index++);
      this.value.encodeOptionally(p, value, result::add);
    }

    return ConfigValueFactory.fromIterable(result);
  }
}
