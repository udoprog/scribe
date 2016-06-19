package eu.toolchain.ogt.typesafe;

import com.typesafe.config.ConfigValue;
import eu.toolchain.ogt.Context;
import eu.toolchain.ogt.EntityDecoder;
import eu.toolchain.ogt.entitybinding.EntityFieldDecoder;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
public class TypeSafeEntityDecoder implements EntityDecoder<ConfigValue> {
    private final Map<String, ConfigValue> value;

    @Override
    public Optional<String> decodeType() {
        return Optional.ofNullable(value.get("type")).map(v -> (String) v.unwrapped());
    }

    @Override
    public Optional<Object> decodeField(
        final EntityFieldDecoder<ConfigValue, Object> entityFieldEncoder, final Context path
    ) {
        return Optional
            .ofNullable(value.get(entityFieldEncoder.getName()))
            .map(n -> entityFieldEncoder.decode(path, n));
    }
}
