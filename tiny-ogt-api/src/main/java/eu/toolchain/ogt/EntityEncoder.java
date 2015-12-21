package eu.toolchain.ogt;

import java.io.IOException;

import eu.toolchain.ogt.binding.FieldMapping;

public interface EntityEncoder {
    void setType(String type) throws IOException;

    void setField(FieldMapping field, Context path, Object value) throws IOException;

    Object encode();
}
