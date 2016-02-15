package eu.toolchain.ogt;

import eu.toolchain.ogt.binding.FieldMapping;

import java.util.Optional;

public interface EntityDecoder<T> {
    Optional<Object> decodeField(FieldMapping field, Context path, T node);

    Optional<String> decodeType(T node);
}
