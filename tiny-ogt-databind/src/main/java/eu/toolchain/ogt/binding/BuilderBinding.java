package eu.toolchain.ogt.binding;

import com.google.common.base.CaseFormat;
import com.google.common.base.Converter;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import eu.toolchain.ogt.Annotations;
import eu.toolchain.ogt.Context;
import eu.toolchain.ogt.EntityDecoder;
import eu.toolchain.ogt.EntityResolver;
import eu.toolchain.ogt.TypeDecoder;
import eu.toolchain.ogt.JavaType;
import eu.toolchain.ogt.creatormethod.CreatorField;
import eu.toolchain.ogt.fieldreader.FieldReader;
import eu.toolchain.ogt.type.TypeMapping;
import lombok.Data;
import lombok.Getter;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Optional;

/**
 * Type binding implementation that uses builder methods for constructing instances.
 *
 * @author udoprog
 */
@Data
public class BuilderBinding<T> implements SetEntityTypeBinding<T> {
    public static final Converter<String, String> LOWER_TO_UPPER =
        CaseFormat.LOWER_CAMEL.converterTo(CaseFormat.UPPER_CAMEL);
    public static final Joiner FIELD_JOINER = Joiner.on(", ");

    private final List<BuilderFieldMapping> fields;
    private final Method newInstance;
    private final Method build;

    @Override
    public List<? extends TypeFieldMapping> fields() {
        return fields;
    }

    @Override
    public Object decodeEntity(
        EntityDecoder<T> entityDecoder, TypeDecoder<T> decoder, Context path
    ) {
        final Object builder;

        try {
            builder = newInstance.invoke(null);
        } catch (final ReflectiveOperationException e) {
            throw new RuntimeException("Failed to create instance of builder (" + newInstance + ")",
                e);
        }

        for (final BuilderFieldMapping m : fields) {
            final Context p = path.push(m.name());

            final Object argument = m
                .type()
                .fromOptional(entityDecoder.decodeField(m, p))
                .orElseThrow(() -> p.error("Missing required field (" + m.name() + ")"));

            try {
                m.getSetter().invoke(builder, argument);
            } catch (final Exception e) {
                throw p.error(
                    "Failed to invoke builder method " + m.name() + " with argument (" + argument +
                        ")", e);
            }
        }

        try {
            return build.invoke(builder);
        } catch (final Exception e) {
            throw new RuntimeException("Could not build instance using " + build, e);
        }
    }

    @Override
    public String toString() {
        return FIELD_JOINER.join(fields);
    }

    public static class BuilderFieldMapping extends TypeFieldMapping {
        @Getter
        private final Method setter;

        public BuilderFieldMapping(
            final String name, final TypeMapping mapping, final FieldReader reader,
            final Method setter
        ) {
            super(name, mapping, reader);
            this.setter = setter;
        }
    }

    public static Optional<Binding> detect(final EntityResolver resolver, final JavaType type) {
        final Method newInstance;

        try {
            newInstance = type.getRawClass().getMethod("builder");
        } catch (final NoSuchMethodException e) {
            return Optional.empty();
        } catch (final Exception e) {
            throw new IllegalArgumentException("Type does not have builder method: " + type);
        }

        if ((newInstance.getModifiers() & Modifier.STATIC) == 0) {
            return Optional.empty();
        }

        final ImmutableList.Builder<BuilderFieldMapping> fields = ImmutableList.builder();

        final Class<?> builderType = newInstance.getReturnType();

        for (final Field field : type.getRawClass().getDeclaredFields()) {
            if ((field.getModifiers() & Modifier.STATIC) != 0) {
                continue;
            }

            final JavaType propertyType = JavaType.construct(field.getGenericType());

            final FieldReader reader = resolver
                .detectFieldReader(type, field.getName(), Optional.of(propertyType))
                .orElseThrow(() -> new IllegalArgumentException(
                    "Can't figure out how to read " + type + " field (" + field.getName() + ")"));

            final Method setter;

            try {
                setter = builderType.getMethod(field.getName(), propertyType.getRawClass());
            } catch (ReflectiveOperationException e) {
                throw new IllegalArgumentException(
                    "Builder does not have method " + builderType.getCanonicalName() + "#" +
                        field.getName() + "(" + propertyType + ")", e);
            }

            if (setter.getParameterTypes().length != 1) {
                throw new IllegalArgumentException(
                    "Builder method (" + field.getName() + ") must take exactly one argument");
            }

            if (!propertyType.equals(JavaType.construct(setter.getGenericParameterTypes()[0]))) {
                throw new IllegalArgumentException(
                    "Builder parameter (" + setter.getParameterTypes()[0] +
                        ") is not assignable to expected (" + propertyType + ")");
            }

            final Annotations annotations = Annotations.of(field.getAnnotations());

            final CreatorField f =
                new CreatorField(annotations, Optional.empty(), Optional.empty());

            final TypeMapping m = resolver.mapping(reader.fieldType(), annotations);
            fields.add(new BuilderFieldMapping(field.getName(), m, reader, setter));
        }

        final Method builderBuild;

        try {
            builderBuild = builderType.getMethod("build");
        } catch (final Exception e) {
            throw new IllegalArgumentException(
                "Missing method #build() on type (" + builderType + ")");
        }

        return Optional.of(new BuilderBinding(fields.build(), newInstance, builderBuild));
    }
}
