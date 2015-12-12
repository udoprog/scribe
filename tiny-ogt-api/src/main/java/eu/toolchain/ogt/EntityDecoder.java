package eu.toolchain.ogt;

import java.util.Optional;

import eu.toolchain.ogt.binding.FieldMapping;

public interface EntityDecoder {
    default void start() {
    }

    default void end() {
    }

    Optional<FieldDecoder> getField(FieldMapping field);

    Optional<String> getType();
}
