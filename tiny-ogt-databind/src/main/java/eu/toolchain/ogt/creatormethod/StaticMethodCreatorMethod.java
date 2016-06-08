package eu.toolchain.ogt.creatormethod;

import com.google.common.collect.ImmutableList;
import eu.toolchain.ogt.JavaType;
import eu.toolchain.ogt.Reflection;
import eu.toolchain.ogt.entitymapper.CreatorMethodDetector;
import lombok.Data;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;

@Data
public class StaticMethodCreatorMethod implements CreatorMethod {
    private final List<CreatorField> fields;
    private final Method method;

    @Override
    public List<CreatorField> fields() {
        return fields;
    }

    @Override
    public InstanceBuilder instanceBuilder() {
        return arguments -> method.invoke(null, arguments.toArray());
    }

    public static CreatorMethodDetector forAnnotation(final Class<? extends Annotation> creator) {
        return (resolver, type) -> {
            final List<Method> methods =
                ImmutableList.copyOf(Reflection.findAnnotatedMethods(type, creator).iterator());

            if (methods.size() == 0) {
                return Optional.empty();
            }

            if (methods.size() > 1) {
                throw new IllegalStateException(
                    String.format("Type must only have one method annotated with @%s, found: %s",
                        creator, methods));
            }

            final Method method = methods.get(0);

            if (!type.equals(JavaType.construct(method.getGenericReturnType()))) {
                throw new IllegalArgumentException(
                    String.format("@%s method must return (%s): %s", creator, type, method));
            }

            final List<CreatorField> fields = resolver.setupCreatorFields(method);
            return Optional.of(new StaticMethodCreatorMethod(fields, method));
        };
    }
}
