package eu.toolchain.ogt;

import eu.toolchain.ogt.entitybinding.EntityFieldDecoder;

import java.util.Optional;

public interface EntityDecoder<T> {
    Optional<Object> decodeField(EntityFieldDecoder<T, Object> entityFieldEncoder, Context path);

    Optional<String> decodeType();
}
