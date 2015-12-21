package eu.toolchain.ogt.type;

import java.io.IOException;
import java.util.Map;

import eu.toolchain.ogt.Context;
import eu.toolchain.ogt.EntityResolver;
import eu.toolchain.ogt.FieldDecoder;
import eu.toolchain.ogt.FieldEncoder;
import eu.toolchain.ogt.JavaType;
import lombok.Data;

@Data
public class MapTypeMapping implements TypeMapping {
    private final JavaType javaType;
    private final TypeMapping key;
    private final TypeMapping value;

    @Override
    public String toString() {
        return "map<" + key + "=" + value + ">";
    }

    @Override
    public JavaType getType() {
        return javaType;
    }

    @Override
    public Object decode(FieldDecoder accessor, Context path) {
        try {
            return accessor.decodeMap(key, value, path);
        } catch (final IOException e) {
            throw path.error("Failed to decode map", e);
        }
    }

    @Override
    public Object encode(FieldEncoder visitor, Context path, Object map) {
        try {
            return visitor.encodeMap(key, value, (Map<?, ?>) map, path);
        } catch (final IOException e) {
            throw path.error("Failed to encode map", e);
        }
    }

    @Override
    public void initialize(final EntityResolver resolver) {
        key.initialize(resolver);
        value.initialize(resolver);
    }
}
