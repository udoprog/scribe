package eu.toolchain.ogt;

import eu.toolchain.ogt.type.EntityTypeMapping;

public interface TypeEncoding<T, O> {
    O encode(T instance);

    T decode(O instance);

    EntityTypeMapping mapping();
}
