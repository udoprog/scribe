package eu.toolchain.ogt.type;

import com.google.common.collect.ImmutableList;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;

import eu.toolchain.ogt.Context;
import eu.toolchain.ogt.EntityResolver;
import eu.toolchain.ogt.FieldDecoder;
import eu.toolchain.ogt.FieldEncoder;
import eu.toolchain.ogt.JavaType;
import eu.toolchain.ogt.Reflection;
import eu.toolchain.ogt.annotations.EntityValue;
import eu.toolchain.ogt.creatormethod.CreatorMethod;
import eu.toolchain.ogt.creatormethod.InstanceBuilder;
import eu.toolchain.ogt.entitymapper.ValueTypeDetector;
import lombok.Data;

/**
 * Represents a type that is constructed using {@link JsonCreator} and {@link JsonValue}
 * annotations.
 *
 * @author udoprog
 */
@Data
public class EntityValueTypeMapping implements TypeMapping {
    public static final Class<? extends Annotation> VALUE = EntityValue.class;

    public static final Optional<Class<? extends Annotation>> JSON_VALUE =
            Reflection.detectPresentAnnotation("com.fasterxml.jackson.annotation.JsonValue");

    private final JavaType sourceType;
    private final TypeMapping mapping;
    private final InstanceBuilder instanceBuilder;
    private final Method valueMethod;

    @Override
    public JavaType getType() {
        return sourceType;
    }

    @Override
    public <T> Object decode(FieldDecoder<T> field, Context path, T instance) {
        final Object value = mapping.decode(field, path, instance);

        try {
            return instanceBuilder.newInstance(ImmutableList.of(value));
        } catch (final Exception e) {
            throw path.error("Could not create instance", e);
        }
    }

    @Override
    public <T> T encode(FieldEncoder<T> encoder, Context path, Object value) {
        final Object v;

        try {
            v = valueMethod.invoke(value);
        } catch (final Exception e) {
            throw path.error("Could not get value from " + value, e);
        }

        return mapping.encode(encoder, path, v);
    }

    @Override
    public void initialize(final EntityResolver resolver) {
        mapping.initialize(resolver);
    }

    public static ValueTypeDetector forAnnotation(
            final Class<? extends Annotation> valueAnnotation) {
        return (resolver, sourceType) -> {
            final List<Method> values = ImmutableList.copyOf(
                    Reflection.findAnnotatedMethods(sourceType, valueAnnotation).iterator());

            final Optional<CreatorMethod> creator = resolver.detectCreatorMethod(sourceType);

            if (values.isEmpty() || !creator.isPresent()) {
                return Optional.empty();
            }

            if (values.size() > 1) {
                throw new IllegalArgumentException(
                        String.format("@%s: Only one method may be annoted, found: %s",
                                VALUE.getSimpleName(), values));
            }

            final CreatorMethod c = creator.get();

            if (c.fields().size() != 1) {
                throw new IllegalArgumentException(String.format(
                        "%s must have exactly one parameter, not %d", c, c.fields().size()));
            }

            final Method value = values.get(0);

            if (value.getParameterTypes().length != 0) {
                throw new IllegalArgumentException(String.format(
                        "@%s method must have no parameters: %s", VALUE.getSimpleName(), value));
            }

            if (!Reflection.isPublic(value)) {
                throw new IllegalArgumentException(String.format("@%s method must be public: %s",
                        VALUE.getSimpleName(), value));
            }

            if (Reflection.isStatic(value)) {
                throw new IllegalArgumentException(String
                        .format("@%s method must not be static: %s", VALUE.getSimpleName(), value));
            }

            /* type to serialize as */
            final TypeMapping targetType =
                    resolver.mapping(JavaType.construct(value.getGenericReturnType()));

            return Optional.of(
                    new EntityValueTypeMapping(sourceType, targetType, c.instanceBuilder(), value));
        };
    }
}
