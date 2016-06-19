package eu.toolchain.ogt.typemapping;

import eu.toolchain.ogt.Context;
import eu.toolchain.ogt.EntityDecoder;
import eu.toolchain.ogt.EntityTypeDecoder;
import lombok.Data;

import java.lang.reflect.Type;
import java.util.Map;

@Data
public class AbstractEntityTypeDecoder<Target> implements EntityTypeDecoder<Target, Object> {
    final Map<String, EntityTypeDecoder<Target, Object>> byName;
    final Map<Type, EntityTypeDecoder<Target, Object>> byType;

    @Override
    public Object decode(final EntityDecoder<Target> encoder, final Context path) {
        final String type = encoder
            .decodeType()
            .orElseThrow(() -> new RuntimeException("No type information available"));

        final EntityTypeDecoder<Target, Object> sub = byName.get(type);

        if (sub == null) {
            throw new RuntimeException(
                "Sub-type (" + type + ") required, but no such type available");
        }

        return sub.decode(encoder, path);
    }
}
