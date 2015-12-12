package eu.toolchain.ogt.creatormethod;

import com.google.common.collect.ImmutableList;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Optional;

import eu.toolchain.ogt.Reflection;
import eu.toolchain.ogt.entitymapper.CreatorMethodDetector;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ConstructorCreatorMethod implements CreatorMethod {
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

    public static CreatorMethodDetector forAnnotation(final Class<? extends Annotation> creator) {
        return (resolver, type) -> {
            final List<Constructor<?>> constructors =
                    ImmutableList.copyOf(Reflection.findAnnotatedConstructors(type, creator)
                            .filter(Reflection::isPublic).iterator());

            if (constructors.isEmpty()) {
                return Optional.empty();
            }

            if (constructors.size() > 1) {
                throw new IllegalStateException(String.format(
                        "Type must only have one public constructor with @%s, found: %s",
                        creator.getSimpleName(), constructors));
            }

            final Constructor<?> constructor = constructors.get(0);

            final List<CreatorField> fields = resolver.setupCreatorFields(constructor);
            return Optional.of(new ConstructorCreatorMethod(fields, constructor));
        };
    }
}
