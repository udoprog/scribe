package eu.toolchain.ogt.type;

import java.io.IOException;
import java.util.Optional;

import eu.toolchain.ogt.Context;
import eu.toolchain.ogt.EntityDecoder;
import eu.toolchain.ogt.FieldDecoder;
import eu.toolchain.ogt.TypeKey;

public interface EntityTypeMapping extends TypeMapping {
    default Optional<String> typeName() {
        return Optional.empty();
    }

    TypeKey key();

    Object decode(EntityDecoder entityDecoder, FieldDecoder decoder,
            Context path) throws IOException;
}
