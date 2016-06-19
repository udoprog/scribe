package eu.toolchain.ogt.entitybinding;

import eu.toolchain.ogt.Context;
import eu.toolchain.ogt.Decoder;
import eu.toolchain.ogt.fieldreader.FieldReader;
import eu.toolchain.ogt.typemapping.TypeMapping;
import lombok.Data;

import java.lang.reflect.Method;
import java.util.Optional;

@Data
class BuilderEntityFieldDecoder<Target> implements EntityFieldDecoder<Target, Object> {
    private final String name;
    private final FieldReader reader;
    private final TypeMapping mapping;
    private final Method setter;
    private final Decoder<Target, Object> parent;

    @Override
    public Object decode(final Context path, final Target instance) {
        return parent.decode(path, instance);
    }

    @Override
    public Optional<?> fromOptional(final Optional<?> value) {
        return parent.fromOptional(value);
    }

    public Method setter() {
        return setter;
    }
}
