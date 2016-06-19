package eu.toolchain.ogt.typemapping;

import eu.toolchain.ogt.Context;
import eu.toolchain.ogt.EntityDecoder;
import eu.toolchain.ogt.EntityTypeDecoder;
import lombok.Data;

import java.util.Optional;

@Data
public class ConcreteEntityTypeDecoder<Target> implements EntityTypeDecoder<Target, Object> {
    private final Optional<String> typeName;
    private final EntityTypeDecoder<Target, Object> decoder;

    @Override
    public Object decode(final EntityDecoder<Target> encoder, final Context path) {
        return decoder.decode(encoder, path);
    }
}
