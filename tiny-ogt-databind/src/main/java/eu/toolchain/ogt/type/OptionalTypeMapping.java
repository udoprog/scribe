package eu.toolchain.ogt.type;

import java.io.IOException;
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
    public Object decode(FieldDecoder accessor, Context path) throws IOException {
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
    public void encode(FieldEncoder visitor, Object inner, Context path) throws IOException {
        value.encode(visitor, inner, path);
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
