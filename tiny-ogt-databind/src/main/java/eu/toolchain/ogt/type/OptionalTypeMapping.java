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
    public Object decode(FieldDecoder accessor, Context path) {
        return value.decode(accessor, path);
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
    public Object encode(FieldEncoder encoder, Context path, Object inner) {
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
