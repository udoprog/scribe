package eu.toolchain.ogt.type;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

import eu.toolchain.ogt.Context;
import eu.toolchain.ogt.EntityDecoder;
import eu.toolchain.ogt.FieldDecoder;
import eu.toolchain.ogt.FieldEncoder;
import eu.toolchain.ogt.JavaType;
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
    public Object decode(FieldDecoder decoder, Context path) {
        try {
            final EntityDecoder entityDecoder = decoder.asEntity();
            return decode(entityDecoder, decoder, path);
        } catch (final IOException e) {
            throw path.error("failed to decode", e);
        }
    }

    @Override   
    public Object decode(EntityDecoder entityDecoder, FieldDecoder decoder, Context path)
            throws IOException {
        final String type = entityDecoder.decodeType()
                .orElseThrow(() -> path.error("No type information available"));

        final EntityTypeMapping sub = subTypes.get(type);

        if (sub == null) {
            throw path.error("Sub-type (" + type + ") required, but no such type available");
        }

        return sub.decode(entityDecoder, decoder, path);
    }

    @Override
    public Object encode(FieldEncoder encoder, Context path, Object value) {
        final EntityTypeMapping sub = subTypesByClass.get(JavaType.construct(value.getClass()));

        if (sub == null) {
            throw path.error("Could not resolve subtype for: " + value);
        }

        return sub.encode(encoder, path, value);
    }
}
