package eu.toolchain.ogt.type;

import com.google.common.base.Joiner;
import eu.toolchain.ogt.Context;
import eu.toolchain.ogt.EntityDecoder;
import eu.toolchain.ogt.EntityEncoder;
import eu.toolchain.ogt.EntityMapper;
import eu.toolchain.ogt.EntityResolver;
import eu.toolchain.ogt.JavaType;
import eu.toolchain.ogt.TypeDecoder;
import eu.toolchain.ogt.TypeEncoder;
import eu.toolchain.ogt.binding.EntityBinding;
import lombok.Data;

import java.io.IOException;
import java.util.Optional;

@Data
public class ConcreteEntityTypeMapping implements EntityTypeMapping {
    public static final Joiner FIELD_JOINER = Joiner.on(", ");

    private final EntityMapper mapper;
    private final JavaType type;
    private final Optional<String> typeName;

    /* left uninitialized to allow for circular dependencies */
    private EntityBinding binding;

    @Override
    public Optional<String> typeName() {
        return typeName;
    }

    @Override
    public <T> Object decode(TypeDecoder<T> decoder, Context path, T instance) {
        final EntityDecoder<T> entityDecoder = decoder.decodeEntity(instance);
        return binding.decodeEntity(entityDecoder, decoder, path);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T encode(TypeEncoder<T> encoder, Context path, Object value) {
        try {
            final EntityEncoder entityEncoder = encoder.newEntityEncoder();

            final Optional<String> typeName = typeName();

            if (typeName.isPresent()) {
                entityEncoder.setType(typeName.get());
            }

            return (T) binding.encodeEntity(entityEncoder, encoder, path, value);
        } catch (final IOException e) {
            throw path.error("Failed to encode entity", e);
        }
    }

    @Override
    public void initialize(final EntityResolver resolver) {
        this.binding = resolver
            .detectBinding(type)
            .orElseThrow(
                () -> new IllegalArgumentException("Cannot detect how to construct type: " + type));
    }

    @Override
    public String toString() {
        final String arguments = FIELD_JOINER.join(
            binding.fields().stream().map(f -> f.name() + "=" + f.type()).iterator());
        return type + "(" + arguments + ")";
    }
}
