package eu.toolchain.ogt.type;

import java.util.Optional;

import eu.toolchain.ogt.Context;
import eu.toolchain.ogt.EntityDecoder;
import eu.toolchain.ogt.EntityEncoder;
import eu.toolchain.ogt.TypeKey;

public interface EntityTypeMapping extends TypeMapping {
    default Optional<String> typeName() {
        return Optional.empty();
    }

    TypeKey key();

    Object decodeEntity(EntityDecoder decoder, Context path);

    void encodeEntity(EntityEncoder encoder, Object value, Context path);
}
