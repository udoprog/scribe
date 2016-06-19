package eu.toolchain.ogt.typemapping;

import eu.toolchain.ogt.EncodingFactory;
import eu.toolchain.ogt.EntityResolver;
import eu.toolchain.ogt.EntityTypeDecoder;
import eu.toolchain.ogt.EntityTypeEncoder;
import eu.toolchain.ogt.entitybinding.EntityBinding;
import lombok.Data;

import java.lang.reflect.Type;
import java.util.Optional;

@Data
public class ConcreteEntityTypeMapping implements EntityTypeMapping {
    private final Type type;
    private final Optional<String> typeName;

    /* left uninitialized to allow for circular dependencies */
    private EntityBinding binding;

    @Override
    public Optional<String> typeName() {
        return typeName;
    }

    @Override
    public <Target> EntityTypeEncoder<Target, Object> newEntityTypeEncoder(
        final EntityResolver resolver, final EncodingFactory<Target> factory
    ) {
        final EntityTypeEncoder<Target, Object> encoding =
            binding.newEntityTypeEncoder(resolver, factory);
        return new ConcreteEntityTypeEncoder<>(typeName, encoding);
    }

    @Override
    public <Target> EntityTypeDecoder<Target, Object> newEntityTypeDecoder(
        final EntityResolver resolver, final EncodingFactory<Target> factory
    ) {
        final EntityTypeDecoder<Target, Object> encoder =
            binding.newEntityTypeDecoder(resolver, factory);
        return new ConcreteEntityTypeDecoder<>(typeName, encoder);
    }

    @Override
    public void initialize(EntityResolver resolver) {
        this.binding = resolver
            .detectBinding(type)
            .orElseThrow(() -> new RuntimeException("Unable to detect binding for type: " + type));
    }
}
