package eu.toolchain.ogt.type;

import java.util.Optional;

public interface EntityTypeMapping extends TypeMapping {
    default Optional<String> typeName() {
        return Optional.empty();
    }
}
