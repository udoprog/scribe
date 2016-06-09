package eu.toolchain.ogt.type;

import eu.toolchain.ogt.Context;
import eu.toolchain.ogt.EntityResolver;
import eu.toolchain.ogt.TypeDecoder;
import eu.toolchain.ogt.TypeEncoder;
import eu.toolchain.ogt.JavaType;
import lombok.Data;

import java.util.Optional;

@Data
public class OptionalTypeMapping implements TypeMapping {
    private final JavaType type;
    private final TypeMapping value;

    @Override
    public JavaType getType() {
        return type;
    }

    @Override
    public <T> Object decode(TypeDecoder<T> decoder, Context path, T instance) {
        return value.decode(decoder, path, instance);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Optional<Object> asOptional(Object value) {
        return (Optional<Object>) value;
    }

    @Override
    public Optional<?> fromOptional(Optional<?> value) {
        return Optional.of(value);
    }

    @Override
    public <T> T encode(TypeEncoder<T> encoder, Context path, Object inner) {
        return value.encode(encoder, path, inner);
    }

    @Override
    public String toString() {
        return "[" + value + "]";
    }

    @Override
    public void initialize(final EntityResolver resolver) {
        value.initialize(resolver);
    }
}
