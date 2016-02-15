package eu.toolchain.ogt.type;

import eu.toolchain.ogt.Context;
import eu.toolchain.ogt.EntityDecoder;
import eu.toolchain.ogt.FieldDecoder;
import eu.toolchain.ogt.FieldEncoder;
import eu.toolchain.ogt.JavaType;
import eu.toolchain.ogt.TypeKey;
import lombok.Data;

import java.util.Map;
import java.util.Optional;

@Data
public class AbstractEntityTypeMapping implements EntityTypeMapping {
    private final JavaType type;
    private final TypeKey key;
    private final Optional<String> typeName;
    private final Map<String, EntityTypeMapping> subTypes;
    private final Map<JavaType, EntityTypeMapping> subTypesByClass;

    @Override
    public TypeKey key() {
        return key;
    }

    @Override
    public Optional<String> typeName() {
        return typeName;
    }

    @Override
    public <T> Object decode(FieldDecoder<T> decoder, Context path, T instance) {
        final EntityDecoder<T> entityDecoder = decoder.newEntityDecoder();

        final String type = entityDecoder
            .decodeType(instance)
            .orElseThrow(() -> path.error("No type information available"));

        final EntityTypeMapping sub = subTypes.get(type);

        if (sub == null) {
            throw path.error("Sub-type (" + type + ") required, but no such type available");
        }

        return sub.decode(decoder, path, instance);
    }

    @Override
    public <T> T encode(FieldEncoder<T> encoder, Context path, Object value) {
        final EntityTypeMapping sub = subTypesByClass.get(JavaType.construct(value.getClass()));

        if (sub == null) {
            throw path.error("Could not resolve subtype for: " + value);
        }

        return sub.encode(encoder, path, value);
    }
}
