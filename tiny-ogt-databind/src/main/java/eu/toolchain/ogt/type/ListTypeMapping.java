package eu.toolchain.ogt.type;

import eu.toolchain.ogt.Context;
import eu.toolchain.ogt.EntityResolver;
import eu.toolchain.ogt.FieldDecoder;
import eu.toolchain.ogt.FieldEncoder;
import eu.toolchain.ogt.JavaType;
import lombok.Data;

import java.io.IOException;
import java.util.List;

@Data
public class ListTypeMapping implements TypeMapping {
    private final JavaType javaType;
    private final TypeMapping value;

    @Override
    public String toString() {
        return "list<" + value + ">";
    }

    @Override
    public JavaType getType() {
        return javaType;
    }

    @Override
    public <T> Object decode(FieldDecoder<T> accessor, Context path, T instance) {
        try {
            return accessor.decodeList(value, path, instance);
        } catch (final IOException e) {
            throw path.error("Failed to decode list", e);
        }
    }

    @Override
    public <T> T encode(FieldEncoder<T> visitor, Context path, Object value) {
        try {
            return visitor.encodeList(this.value, (List<?>) value, path);
        } catch (final IOException e) {
            throw path.error("Failed to encode list", e);
        }
    }

    @Override
    public void initialize(final EntityResolver resolver) {
        value.initialize(resolver);
    }
}
