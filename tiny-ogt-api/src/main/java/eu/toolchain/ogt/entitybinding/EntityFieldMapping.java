package eu.toolchain.ogt.entitybinding;

import eu.toolchain.ogt.EncodingFactory;
import eu.toolchain.ogt.EntityResolver;

import java.util.Optional;

public interface EntityFieldMapping {
    <T> Optional<? extends EntityFieldEncoder<T, Object>> newEntityFieldEncoder(
        EntityResolver resolver, EncodingFactory<T> factory
    );

    <T> Optional<? extends EntityFieldDecoder<T, Object>> newEntityFieldDecoder(
        EntityResolver resolver, EncodingFactory<T> factory
    );
}
