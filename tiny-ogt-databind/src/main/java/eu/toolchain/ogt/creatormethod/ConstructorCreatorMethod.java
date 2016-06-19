package eu.toolchain.ogt.creatormethod;

import eu.toolchain.ogt.EntityField;
import eu.toolchain.ogt.Match;
import eu.toolchain.ogt.Priority;
import eu.toolchain.ogt.Reflection;
import eu.toolchain.ogt.entitymapper.CreatorMethodDetector;
import lombok.RequiredArgsConstructor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.util.List;

@RequiredArgsConstructor
public class ConstructorCreatorMethod implements CreatorMethod {
    private final List<EntityField> fields;
    private final Constructor<?> constructor;

    @Override
    public List<EntityField> fields() {
        return fields;
    }

    @Override
    public InstanceBuilder instanceBuilder() {
        return arguments -> {
            try {
                return constructor.newInstance(arguments.toArray());
            } catch (final Exception e) {
                throw new RuntimeException(e);
            }
        };
    }

    public static CreatorMethodDetector forAnnotation(final Class<? extends Annotation> creator) {
        return (resolver, type) -> Reflection
            .findByAnnotation(type, creator, Class::getDeclaredConstructors)
            .filter(Reflection::isPublic)
            .map(c -> {
                final List<EntityField> fields = resolver.detectExecutableFields(c);
                return new ConstructorCreatorMethod(fields, c);
            })
            .map(Match.withPriority(Priority.HIGH));
    }
}
