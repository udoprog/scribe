package eu.toolchain.ogt.type;

import eu.toolchain.ogt.TypeKey;

import java.util.Optional;

public interface EntityTypeMapping extends TypeMapping {
    default Optional<String> typeName() {
        return Optional.empty();
    }

    TypeKey key();
}
