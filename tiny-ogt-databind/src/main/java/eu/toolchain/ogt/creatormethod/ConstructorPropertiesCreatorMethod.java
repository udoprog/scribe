package eu.toolchain.ogt.creatormethod;

import com.google.common.collect.ImmutableList;
import eu.toolchain.ogt.Annotations;
import eu.toolchain.ogt.EntityResolver;
import eu.toolchain.ogt.JavaType;
import eu.toolchain.ogt.Reflection;
import lombok.RequiredArgsConstructor;

import java.beans.ConstructorProperties;
import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class ConstructorPropertiesCreatorMethod implements CreatorMethod {
    private final List<CreatorField> fields;
    private final Constructor<?> constructor;

    @Override
    public List<CreatorField> fields() {
        return fields;
    }

    @Override
    public InstanceBuilder instanceBuilder() {
        return arguments -> constructor.newInstance(arguments.toArray());
    }

    public static Optional<CreatorMethod> detect(
        final EntityResolver resolver, final JavaType type
    ) {
        final List<Constructor<?>> constructors = ImmutableList.copyOf(Reflection
            .findAnnotatedConstructors(type, ConstructorProperties.class)
            .filter(Reflection::isPublic)
            .iterator());

        if (constructors.isEmpty()) {
            return Optional.empty();
        }

        if (constructors.size() > 1) {
            throw new IllegalStateException(
                String.format("Type must only have one public constructor with @%s, found: %s",
                    ConstructorProperties.class.getSimpleName(), constructors));
        }

        final Constructor<?> constructor = constructors.get(0);

        final ConstructorProperties properties =
            constructor.getAnnotation(ConstructorProperties.class);

        final ImmutableList.Builder<CreatorField> fields = ImmutableList.builder();

        for (final String name : properties.value()) {
            final Annotations annotations = resolver.detectFieldAnnotations(type, name);

            fields.add(
                resolver.setupCreatorField(annotations, Optional.empty(), Optional.of(name)));
        }

        return Optional.of(new ConstructorPropertiesCreatorMethod(fields.build(), constructor));
    }
}
