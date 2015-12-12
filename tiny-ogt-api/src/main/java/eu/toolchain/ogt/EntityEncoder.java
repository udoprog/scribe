package eu.toolchain.ogt;

import java.io.IOException;

import eu.toolchain.ogt.binding.FieldMapping;

public interface EntityEncoder {
    default void startEntity() throws IOException {
    }

    default void endEntity() throws IOException {
    }

    void setType(String type) throws IOException;

    FieldEncoder setField(FieldMapping field) throws IOException;
}
