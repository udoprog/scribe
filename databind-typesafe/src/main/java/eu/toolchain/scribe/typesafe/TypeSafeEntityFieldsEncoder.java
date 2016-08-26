package eu.toolchain.scribe.typesafe;

import com.typesafe.config.ConfigValue;
import com.typesafe.config.ConfigValueFactory;
import eu.toolchain.scribe.Context;
import eu.toolchain.scribe.EntityFieldsEncoder;
import eu.toolchain.scribe.entitymapping.EntityFieldEncoder;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class TypeSafeEntityFieldsEncoder implements EntityFieldsEncoder<ConfigValue> {
  final Map<String, ConfigValue> object = new HashMap<>();

  @Override
  public <Source> void encodeField(
      EntityFieldEncoder<ConfigValue, Source> field, Context path, Source value
  ) {
    object.put(field.getName(), field.encode(path, value));
  }

  @Override
  public ConfigValue buildEmpty(final Context path) {
    return ConfigValueFactory.fromAnyRef(null);
  }

  @Override
  public ConfigValue build() {
    return ConfigValueFactory.fromMap(object);
  }
}
