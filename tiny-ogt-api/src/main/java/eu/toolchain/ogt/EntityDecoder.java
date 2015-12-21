package eu.toolchain.ogt;

import java.util.Optional;

import eu.toolchain.ogt.binding.FieldMapping;

public interface EntityDecoder {
    Optional<Object> decodeField(FieldMapping field, Context path);

    Optional<String> decodeType();
}
