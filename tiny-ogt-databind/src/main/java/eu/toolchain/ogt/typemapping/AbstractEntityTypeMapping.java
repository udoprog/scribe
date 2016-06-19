package eu.toolchain.ogt.typemapping;

import com.google.common.collect.ImmutableMap;
import eu.toolchain.ogt.EncodingFactory;
import eu.toolchain.ogt.EntityResolver;
import eu.toolchain.ogt.EntityTypeDecoder;
import eu.toolchain.ogt.EntityTypeEncoder;
import eu.toolchain.ogt.type.JavaType;
import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Data
public class AbstractEntityTypeMapping implements EntityTypeMapping {
    private final JavaType type;
    private final Optional<String> typeName;
    private final List<EntityTypeMapping> subTypes;

    @Override
    public Optional<String> typeName() {
        return typeName;
    }

    @Override
    public <Target> EntityTypeEncoder<Target, Object> newEntityTypeEncoder(
        final EntityResolver resolver, final EncodingFactory<Target> factory
    ) {
        final ImmutableMap.Builder<JavaType, EntityTypeEncoder<Target, Object>> byTypeBuilder =
            ImmutableMap.builder();

        for (final EntityTypeMapping subType : this.subTypes) {
            final EntityTypeEncoder<Target, Object> encoding =
                subType.newEntityTypeEncoder(resolver, factory);

            byTypeBuilder.put(subType.getType(), encoding);
        }

        final Map<JavaType, EntityTypeEncoder<Target, Object>> byType = byTypeBuilder.build();
        return new AbstractEntityTypeEncoder<>(byType);
    }

    @Override
    public <Target> EntityTypeDecoder<Target, Object> newEntityTypeDecoder(
        final EntityResolver resolver, final EncodingFactory<Target> factory
    ) {
        final ImmutableMap.Builder<String, EntityTypeDecoder<Target, Object>> byNameBuilder =
            ImmutableMap.builder();

        for (final EntityTypeMapping subType : this.subTypes) {
            final EntityTypeDecoder<Target, Object> encoding =
                subType.newEntityTypeDecoder(resolver, factory);

            byNameBuilder.put(subType
                .typeName()
                .orElseThrow(() -> new RuntimeException("Name required for " + subType)), encoding);
        }

        final Map<String, EntityTypeDecoder<Target, Object>> byName = byNameBuilder.build();
        return new AbstractEntityTypeDecoder<>(byName);
    }
}
