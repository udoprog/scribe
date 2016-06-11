package eu.toolchain.ogt.type;

import eu.toolchain.ogt.Context;
import eu.toolchain.ogt.EntityResolver;
import eu.toolchain.ogt.JavaType;
import eu.toolchain.ogt.TypeDecoder;
import eu.toolchain.ogt.TypeEncoder;
import lombok.Data;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
public class ObjectArrayTypeMapping implements TypeMapping {
    private final JavaType type;
    private final TypeMapping component;

    @Override
    public JavaType getType() {
        return type;
    }

    @Override
    public <T> Object decode(TypeDecoder<T> accessor, Context path, T instance) {
        try {
            return accessor.decodeList(component, path, instance).toArray();
        } catch (final IOException e) {
            throw path.error("Failed to decode bytes");
        }
    }

    @Override
    public <T> T encode(TypeEncoder<T> encoder, Context path, Object value) {
        try {
            final List<?> values = Stream.of((Object[]) value).collect(Collectors.toList());
            return encoder.encodeList(component, values, path);
        } catch (final IOException e) {
            throw path.error("Failed to encode bytes", e);
        }
    }

    @Override
    public String toString() {
        return component.toString() + "[]";
    }

    @Override
    public void initialize(final EntityResolver resolver) {
        component.initialize(resolver);
    }
}
