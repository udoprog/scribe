package eu.toolchain.ogt;

import eu.toolchain.ogt.type.TypeMapping;

public interface TypeEncoding<T, O> {
    O encode(T instance);

    T decode(O instance);

    TypeMapping mapping();
}
