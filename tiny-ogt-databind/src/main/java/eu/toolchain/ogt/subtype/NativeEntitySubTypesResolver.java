package eu.toolchain.ogt.subtype;

import com.google.common.collect.ImmutableMap;

import java.util.Map;
import java.util.Optional;

import eu.toolchain.ogt.EntityResolver;
import eu.toolchain.ogt.JavaType;
import eu.toolchain.ogt.annotations.EntitySubTypes;
import eu.toolchain.ogt.type.EntityTypeMapping;
import eu.toolchain.ogt.type.TypeMapping;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class NativeEntitySubTypesResolver implements EntitySubTypesProvider {
    private final Map<String, EntityTypeMapping> subtypes;

    @Override
    public Map<String, EntityTypeMapping> subtypes() {
        return subtypes;
    }

    public static Optional<EntitySubTypesProvider> detect(final EntityResolver resolver,
            final JavaType type) {
        final Optional<EntitySubTypes> annotation =
                Optional.ofNullable(type.getRawClass().getAnnotation(EntitySubTypes.class));

        return annotation.map(a -> {
            final ImmutableMap.Builder<String, EntityTypeMapping> subtypes = ImmutableMap.builder();

            for (final EntitySubTypes.Type s : a.value()) {
                final TypeMapping sub = resolver.mapping(s.value());

                if (!(sub instanceof EntityTypeMapping)) {
                    throw new IllegalArgumentException("Not an entity: " + s.value());
                }

                final EntityTypeMapping e = (EntityTypeMapping) sub;

                final String name = e.typeName().orElseThrow(() -> new IllegalStateException(
                        "Class must have a name annotation: " + s.value()));

                subtypes.put(name, e);
            }

            return new NativeEntitySubTypesResolver(subtypes.build());
        });
    }
}
