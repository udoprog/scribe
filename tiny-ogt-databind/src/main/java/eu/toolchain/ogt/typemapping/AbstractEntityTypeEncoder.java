package eu.toolchain.ogt.typemapping;

import eu.toolchain.ogt.Context;
import eu.toolchain.ogt.EntityEncoder;
import eu.toolchain.ogt.EntityTypeEncoder;
import eu.toolchain.ogt.type.JavaType;
import lombok.Data;

import java.util.Map;

@Data
public class AbstractEntityTypeEncoder<T> implements EntityTypeEncoder<T, Object> {
    final Map<JavaType, EntityTypeEncoder<T, Object>> byType;

    @Override
    public T encode(
        final EntityEncoder<T> decoder, final Context path, final Object instance
    ) {
        final EntityTypeEncoder<T, Object> sub = byType.get(JavaType.of(instance.getClass()));

        if (sub == null) {
            throw new RuntimeException("Could not resolve subtype for: " + instance);
        }

        return sub.encode(decoder, path, instance);
    }
}
