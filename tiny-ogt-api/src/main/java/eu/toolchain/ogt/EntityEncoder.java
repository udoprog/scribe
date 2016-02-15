package eu.toolchain.ogt;

import eu.toolchain.ogt.binding.FieldMapping;

import java.io.IOException;

public interface EntityEncoder<T> {
    void setType(String type) throws IOException;

    void setField(FieldMapping field, Context path, Object value) throws IOException;

    T build();
}
