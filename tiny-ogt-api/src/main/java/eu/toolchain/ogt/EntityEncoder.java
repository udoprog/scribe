package eu.toolchain.ogt;

import eu.toolchain.ogt.entitybinding.EntityFieldEncoder;

public interface EntityEncoder<T> {
    void setType(String type);

    void setField(EntityFieldEncoder<T, Object> field, Context path, Object value);

    T build();
}
