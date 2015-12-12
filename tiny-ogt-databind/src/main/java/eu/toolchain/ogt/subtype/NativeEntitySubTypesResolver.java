package eu.toolchain.ogt.subtype;

import com.google.common.collect.ImmutableMap;

import java.util.Map;
import java.util.Optional;

import eu.toolchain.ogt.EntityResolver;
import eu.toolchain.ogt.JavaType;
import eu.toolchain.ogt.annotations.EntitySubTypes;
import eu.toolchain.ogt.type.EntityTypeMapping;
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
                final EntityTypeMapping sub = resolver.mapping(s.value());

                final String name = sub.typeName().orElseThrow(() -> new IllegalStateException(
                        "Class must have a name annotation: " + s.value()));

                subtypes.put(name, sub);
            }

            return new NativeEntitySubTypesResolver(subtypes.build());
        });
    }
}
