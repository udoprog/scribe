package eu.toolchain.ogt.entitybinding;

import eu.toolchain.ogt.EncodingFactory;
import eu.toolchain.ogt.EntityResolver;
import eu.toolchain.ogt.fieldreader.FieldReader;
import eu.toolchain.ogt.typemapping.TypeMapping;
import lombok.Data;

import java.util.Optional;

@Data
class ConstructorEntityFieldMapping implements EntityFieldMapping {
    private final String name;
    private final TypeMapping mapping;
    private final FieldReader reader;

    @Override
    public <Target> Optional<EntityFieldEncoder<Target, Object>> newEntityFieldEncoder(
        final EntityResolver resolver, final EncodingFactory<Target> factory
    ) {
        return mapping
            .newEncoder(resolver, factory)
            .map(parent -> new ConstructorEntityFieldEncoder<>(parent, name, reader));
    }

    @Override
    public <T> Optional<? extends EntityFieldDecoder<T, Object>> newEntityFieldDecoder(
        final EntityResolver resolver, final EncodingFactory<T> factory
    ) {
        return mapping
            .newDecoder(resolver, factory)
            .map(parent -> new ConstructorEntityFieldDecoder<>(parent, name));
    }
}
