package eu.toolchain.ogt.type;

import java.io.IOException;
import java.util.Optional;

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
    public Object decode(FieldDecoder decoder, Context path) {
        try {
            final EntityDecoder entityDecoder = decoder.asEntity();
            return decode(entityDecoder, decoder, path);
        } catch (final IOException e) {
            throw path.error("Failed to decode entity", e);
        }
    }

    @Override
    public Object decode(EntityDecoder entityDecoder, FieldDecoder decoder, Context path)
            throws IOException {
        return binder.decodeEntity(entityDecoder, decoder, path);
    }

    @Override
    public Object encode(FieldEncoder encoder, Context path, Object value) {
        try {
            final EntityEncoder entityEncoder = encoder.encodeEntity();

            final Optional<String> typeName = typeName();

            if (typeName.isPresent()) {
                entityEncoder.setType(typeName.get());
            }

            return binder.encodeEntity(entityEncoder, encoder, value, path);
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
        this.binder = resolver.detectBinding(type).orElseThrow(
                () -> new IllegalArgumentException("Cannot detect how to construct type: " + type));
    }
}
