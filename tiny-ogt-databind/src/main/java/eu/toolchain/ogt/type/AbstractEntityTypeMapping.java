package eu.toolchain.ogt.type;

import com.google.common.base.Joiner;
import eu.toolchain.ogt.Context;
import eu.toolchain.ogt.EntityDecoder;
import eu.toolchain.ogt.JavaType;
import eu.toolchain.ogt.TypeDecoder;
import eu.toolchain.ogt.TypeEncoder;
import lombok.Data;

import java.util.Map;
import java.util.Optional;

@Data
public class AbstractEntityTypeMapping implements EntityTypeMapping {
    public static final Joiner FIELD_JOINER = Joiner.on(", ");

    private final JavaType type;
    private final Optional<String> typeName;
    private final Map<String, EntityTypeMapping> subTypes;
    private final Map<JavaType, EntityTypeMapping> subTypesByClass;

    @Override
    public Optional<String> typeName() {
        return typeName;
    }

    @Override
    public <T> Object decode(TypeDecoder<T> decoder, Context path, T instance) {
        final EntityDecoder<T> entityDecoder = decoder.decodeEntity(instance);

        final String type = entityDecoder
            .decodeType()
            .orElseThrow(() -> path.error("No type information available"));

        final EntityTypeMapping sub = subTypes.get(type);

        if (sub == null) {
            throw path.error("Sub-type (" + type + ") required, but no such type available");
        }

        return sub.decode(decoder, path, instance);
    }

    @Override
    public <T> T encode(TypeEncoder<T> encoder, Context path, Object value) {
        final EntityTypeMapping sub = subTypesByClass.get(JavaType.construct(value.getClass()));

        if (sub == null) {
            throw path.error("Could not resolve subtype for: " + value);
        }

        return sub.encode(encoder, path, value);
    }

    @Override
    public String toString() {
        return type + "(subTypes=" + subTypes + ")";
    }
}
