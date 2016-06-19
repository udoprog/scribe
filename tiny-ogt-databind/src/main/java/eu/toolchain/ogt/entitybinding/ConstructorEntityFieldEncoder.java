package eu.toolchain.ogt.entitybinding;

import eu.toolchain.ogt.Context;
import eu.toolchain.ogt.Encoder;
import eu.toolchain.ogt.fieldreader.FieldReader;
import lombok.Data;

import java.util.Optional;

@Data
public class ConstructorEntityFieldEncoder<Target> implements EntityFieldEncoder<Target, Object> {
    private final Encoder<Target, Object> parent;
    private final String name;
    private final FieldReader reader;

    @Override
    public Target encode(final Context path, final Object instance) {
        return parent.encode(path, instance);
    }

    @Override
    public Optional<Object> asOptional(final Object value) {
        return parent.asOptional(value);
    }
}
