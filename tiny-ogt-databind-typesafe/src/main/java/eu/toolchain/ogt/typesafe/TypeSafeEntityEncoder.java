package eu.toolchain.ogt.typesafe;

import com.google.common.collect.ImmutableMap;
import com.typesafe.config.ConfigValue;
import com.typesafe.config.ConfigValueFactory;
import eu.toolchain.ogt.Context;
import eu.toolchain.ogt.EntityEncoder;
import eu.toolchain.ogt.entitybinding.EntityFieldEncoder;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TypeSafeEntityEncoder implements EntityEncoder<ConfigValue> {
    final ImmutableMap.Builder<String, ConfigValue> object = ImmutableMap.builder();

    @Override
    public void setField(
        EntityFieldEncoder<ConfigValue, Object> field, Context path, Object value
    ) {
        object.put(field.getName(), field.encode(path, value));
    }

    @Override
    public void setType(String type) {
        object.put("type", ConfigValueFactory.fromAnyRef(type));
    }

    @Override
    public ConfigValue build() {
        return ConfigValueFactory.fromMap(object.build());
    }
}
