package eu.toolchain.ogt.entitybinding;

import eu.toolchain.ogt.Context;
import eu.toolchain.ogt.Encoder;
import eu.toolchain.ogt.fieldreader.FieldReader;
import eu.toolchain.ogt.typemapping.TypeMapping;
import lombok.Data;

import java.lang.reflect.Method;
import java.util.Optional;

@Data
class BuilderEntityFieldEncoder<Target> implements EntityFieldEncoder<Target, Object> {
    private final String name;
    private final FieldReader reader;
    private final TypeMapping mapping;
    private final Method setter;
    private final Encoder<Target, Object> parent;

    @Override
    public Target encode(final Context path, final Object instance) {
        return parent.encode(path, instance);
    }

    @Override
    public Optional<Object> asOptional(final Object value) {
        return parent.asOptional(value);
    }

    public Method setter() {
        return setter;
    }
}
