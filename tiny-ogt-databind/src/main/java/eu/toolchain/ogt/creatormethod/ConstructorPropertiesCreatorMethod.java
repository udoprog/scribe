package eu.toolchain.ogt.creatormethod;

import eu.toolchain.ogt.EntityField;
import eu.toolchain.ogt.EntityResolver;
import eu.toolchain.ogt.type.AccessibleType;
import eu.toolchain.ogt.type.JavaType;
import eu.toolchain.ogt.Match;
import eu.toolchain.ogt.Priority;
import lombok.RequiredArgsConstructor;

import java.beans.ConstructorProperties;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
public class ConstructorPropertiesCreatorMethod implements CreatorMethod {
    private final List<EntityField> fields;
    private final JavaType.Constructor constructor;

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
        final EntityResolver resolver, final JavaType type
    ) {
        return type
            .findByAnnotation(JavaType::getConstructors, ConstructorProperties.class)
            .filter(AccessibleType::isPublic)
            .flatMap(c -> {
                return c.getAnnotation(ConstructorProperties.class).map(properties -> {
                    final List<EntityField> fields = resolver.detectExecutableFields(c);

                    if (properties.value().length != fields.size()) {
                        throw new IllegalStateException(String.format(
                            "The number of parameters for constructor %s, does not match provided" +
                                " by " +
                                "@%s", c, ConstructorProperties.class.getSimpleName()));
                    }

                    final List<EntityField> named = fields
                        .stream()
                        .map(e -> e.withName(properties.value()[e.getIndex()]))
                        .collect(Collectors.toList());

                    return new ConstructorPropertiesCreatorMethod(named, c);
                });
            })
            .map(Match.withPriority(Priority.HIGH));
    }
}
