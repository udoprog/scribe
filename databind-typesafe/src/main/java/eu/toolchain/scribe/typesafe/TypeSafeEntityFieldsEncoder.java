package eu.toolchain.scribe.typesafe;

import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigObject;
import com.typesafe.config.ConfigValue;
import eu.toolchain.scribe.Context;
import eu.toolchain.scribe.EntityFieldEncoder;
import eu.toolchain.scribe.EntityFieldsEncoder;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class TypeSafeEntityFieldsEncoder implements EntityFieldsEncoder<ConfigValue, ConfigObject> {
  final Map<String, ConfigValue> object = new HashMap<>();

  @Override
  public <Source> void encodeField(
      EntityFieldEncoder<ConfigValue, Source> field, Context path, Source value
  ) {
    field.encodeOptionally(path.push(field.getName()), value,
        target -> object.put(field.getName(), target));
  }

  @Override
  public ConfigObject buildEmpty(final Context path) {
    return ConfigFactory.empty().root();
  }

  @Override
  public ConfigObject build() {
    return ConfigFactory.parseMap(object).root();
  }
}
