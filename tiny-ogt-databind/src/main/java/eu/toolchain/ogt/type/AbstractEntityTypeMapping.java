package eu.toolchain.ogt.type;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

import eu.toolchain.ogt.Context;
import eu.toolchain.ogt.FieldDecoder;
import eu.toolchain.ogt.FieldEncoder;
import eu.toolchain.ogt.JavaType;
import eu.toolchain.ogt.EntityDecoder;
import eu.toolchain.ogt.EntityEncoder;
import eu.toolchain.ogt.TypeKey;
import lombok.Data;

@Data
public class AbstractEntityTypeMapping<T> implements EntityTypeMapping {
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
    public Object decode(FieldDecoder decoder, Context path) throws IOException {
        return decodeEntity(decoder.asEntity(), path);
    }

    @Override
    public void encode(FieldEncoder encoder, Object value, Context path) throws IOException {
        encodeEntity(encoder.setEntity(), value, path);
    }

    @Override
    public Object decodeEntity(EntityDecoder decoder, Context path) {
        final String type =
                decoder.getType().orElseThrow(() -> path.error("No type information available"));

        final EntityTypeMapping sub = subTypes.get(type);

        if (sub == null) {
            throw path.error("Sub-type (" + type + ") required, but no such type available");
        }

        return sub.decodeEntity(decoder, path);
    }

    @Override
    public void encodeEntity(EntityEncoder encoder, Object value, Context path) {
        final EntityTypeMapping sub = subTypesByClass.get(JavaType.construct(value.getClass()));

        if (sub == null) {
            throw path.error("Could not resolve subtype for: " + value);
        }

        sub.encodeEntity(encoder, value, path);
    }
}
