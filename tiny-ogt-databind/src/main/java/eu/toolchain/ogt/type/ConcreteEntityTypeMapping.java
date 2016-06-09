package eu.toolchain.ogt.type;

import eu.toolchain.ogt.Context;
import eu.toolchain.ogt.EntityDecoder;
import eu.toolchain.ogt.EntityEncoder;
import eu.toolchain.ogt.EntityMapper;
import eu.toolchain.ogt.EntityResolver;
import eu.toolchain.ogt.FieldDecoder;
import eu.toolchain.ogt.FieldEncoder;
import eu.toolchain.ogt.JavaType;
import eu.toolchain.ogt.TypeKey;
import eu.toolchain.ogt.binding.Binding;
import lombok.Data;

import java.io.IOException;
import java.util.Optional;

@Data
public class ConcreteEntityTypeMapping implements EntityTypeMapping {
    private final EntityMapper mapper;
    private final JavaType type;
    private final TypeKey key;
    private final Optional<String> typeName;

    /* left uninitialized to allow for circular dependencies */
    private Binding binder;

    @Override
    public TypeKey key() {
        return key;
    }

    @Override
    public Optional<String> typeName() {
        return typeName;
    }

    @Override
    public <T> Object decode(FieldDecoder<T> decoder, Context path, T instance) {
        final EntityDecoder<T> entityDecoder = decoder.decodeEntity(instance);
        return binder.decodeEntity(entityDecoder, decoder, path);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T encode(FieldEncoder<T> encoder, Context path, Object value) {
        try {
            final EntityEncoder entityEncoder = encoder.newEntityEncoder();

            final Optional<String> typeName = typeName();

            if (typeName.isPresent()) {
                entityEncoder.setType(typeName.get());
            }

            return (T) binder.encodeEntity(entityEncoder, encoder, path, value);
        } catch (final IOException e) {
            throw path.error("Failed to encode entity", e);
        }
    }

    @Override
    public String toString() {
        return type + "(" + binder.toString() + ")";
    }

    @Override
    public void initialize(final EntityResolver resolver) {
        this.binder = resolver
            .detectBinding(type)
            .orElseThrow(
                () -> new IllegalArgumentException("Cannot detect how to construct type: " + type));
    }
}
