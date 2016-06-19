package eu.toolchain.ogt.creatormethod;

import eu.toolchain.ogt.EntityField;
import eu.toolchain.ogt.Match;
import eu.toolchain.ogt.Priority;
import eu.toolchain.ogt.entitymapper.CreatorMethodDetector;
import lombok.Data;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;

import static eu.toolchain.ogt.Reflection.findByAnnotation;

@Data
public class StaticMethodCreatorMethod implements CreatorMethod {
    private final List<EntityField> fields;
    private final Method method;

    @Override
    public List<EntityField> fields() {
        return fields;
    }

    @Override
    public InstanceBuilder instanceBuilder() {
        return arguments -> {
            try {
                return method.invoke(null, arguments.toArray());
            } catch (final Exception e) {
                throw new RuntimeException(e);
            }
        };
    }

    public static CreatorMethodDetector forAnnotation(final Class<? extends Annotation> creator) {
        return (resolver, type) -> findByAnnotation(type, creator, Class::getDeclaredMethods)
            .map(m -> {
                if (!type.equals(m.getGenericReturnType())) {
                    throw new IllegalArgumentException(
                        String.format("@%s method must return (%s): %s", creator, type, m));
                }

                final List<EntityField> fields = resolver.detectExecutableFields(m);
                return new StaticMethodCreatorMethod(fields, m);
            })
            .map(Match.withPriority(Priority.HIGH));
    }
}
