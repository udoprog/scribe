package eu.toolchain.ogt.entitybinding;

import eu.toolchain.ogt.Context;
import eu.toolchain.ogt.Decoder;
import lombok.Data;

import java.util.Optional;

@Data
public class ConstructorEntityFieldDecoder<Target> implements EntityFieldDecoder<Target, Object> {
    private final Decoder<Target, Object> parent;
    private final String name;

    @Override
    public Object decode(final Context path, final Target instance) {
        return parent.decode(path, instance);
    }

    @Override
    public Optional<?> fromOptional(final Optional<?> value) {
        return parent.fromOptional(value);
    }
}
