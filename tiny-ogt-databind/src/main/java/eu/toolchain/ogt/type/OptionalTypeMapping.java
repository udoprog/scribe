package eu.toolchain.ogt.type;

import java.util.Optional;

import eu.toolchain.ogt.Context;
import eu.toolchain.ogt.EntityResolver;
import eu.toolchain.ogt.FieldDecoder;
import eu.toolchain.ogt.FieldEncoder;
import eu.toolchain.ogt.JavaType;
import lombok.Data;

@Data
public class OptionalTypeMapping implements TypeMapping {
    private final JavaType type;
    private final TypeMapping value;

    @Override
    public JavaType getType() {
        return type;
    }

    @Override
    public <T> Object decode(FieldDecoder<T> decoder, Context path, T instance) {
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
    public <T> T encode(FieldEncoder<T> encoder, Context path, Object inner) {
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
