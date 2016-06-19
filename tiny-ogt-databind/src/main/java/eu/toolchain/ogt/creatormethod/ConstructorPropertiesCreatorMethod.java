package eu.toolchain.ogt.creatormethod;

import eu.toolchain.ogt.EntityField;
import eu.toolchain.ogt.EntityResolver;
import eu.toolchain.ogt.Match;
import eu.toolchain.ogt.Priority;
import eu.toolchain.ogt.Reflection;
import lombok.RequiredArgsConstructor;

import java.beans.ConstructorProperties;
import java.lang.reflect.Constructor;
import java.lang.reflect.Type;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static eu.toolchain.ogt.Reflection.findByAnnotation;

@RequiredArgsConstructor
public class ConstructorPropertiesCreatorMethod implements CreatorMethod {
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

    public static Stream<Match<CreatorMethod>> detect(
        final EntityResolver resolver, final Type type
    ) {
        return findByAnnotation(type, ConstructorProperties.class, Class::getDeclaredConstructors)
            .filter(Reflection::isPublic)
            .map(c -> {
                final ConstructorProperties properties =
                    c.getAnnotation(ConstructorProperties.class);

                final List<EntityField> fields = resolver.detectExecutableFields(c);

                if (properties.value().length != fields.size()) {
                    throw new IllegalStateException(String.format(
                        "The number of parameters for constructor %s, does not match provided by " +
                            "@%s", c, ConstructorProperties.class.getSimpleName()));
                }

                final List<EntityField> named = fields
                    .stream()
                    .map(e -> e.withName(properties.value()[e.getIndex()]))
                    .collect(Collectors.toList());

                return new ConstructorPropertiesCreatorMethod(named, c);
            })
            .map(Match.withPriority(Priority.HIGH));
    }
}
