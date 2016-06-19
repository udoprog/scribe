package eu.toolchain.ogt.entitybinding;

import eu.toolchain.ogt.EncodingFactory;
import eu.toolchain.ogt.EntityResolver;
import eu.toolchain.ogt.fieldreader.FieldReader;
import eu.toolchain.ogt.typemapping.TypeMapping;
import lombok.Data;

import java.lang.reflect.Method;
import java.util.Optional;

@Data
class BuilderEntityFieldMapping implements EntityFieldMapping {
    private final String name;
    private final TypeMapping mapping;
    private final FieldReader reader;
    private final Method setter;

    @Override
    public <Target> Optional<BuilderEntityFieldEncoder<Target>> newEntityFieldEncoder(
        final EntityResolver resolver, final EncodingFactory<Target> factory
    ) {
        return mapping
            .newEncoder(resolver, factory)
            .map(parent -> new BuilderEntityFieldEncoder<>(name, reader, mapping, setter, parent));
    }

    @Override
    public <Target> Optional<BuilderEntityFieldDecoder<Target>> newEntityFieldDecoder(
        final EntityResolver resolver, final EncodingFactory<Target> factory
    ) {
        return mapping
            .newDecoder(resolver, factory)
            .map(parent -> new BuilderEntityFieldDecoder<>(name, reader, mapping, setter, parent));
    }
}
