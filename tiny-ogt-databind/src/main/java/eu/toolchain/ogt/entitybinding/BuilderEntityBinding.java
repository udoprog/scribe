package eu.toolchain.ogt.entitybinding;

import com.google.common.base.CaseFormat;
import com.google.common.base.Converter;
import com.google.common.collect.ImmutableList;
import eu.toolchain.ogt.Annotations;
import eu.toolchain.ogt.EncodingFactory;
import eu.toolchain.ogt.EntityResolver;
import eu.toolchain.ogt.EntityTypeDecoder;
import eu.toolchain.ogt.EntityTypeEncoder;
import eu.toolchain.ogt.Match;
import eu.toolchain.ogt.Methods;
import eu.toolchain.ogt.Priority;
import eu.toolchain.ogt.Reflection;
import eu.toolchain.ogt.fieldreader.FieldReader;
import eu.toolchain.ogt.typemapping.TypeMapping;
import lombok.Data;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Entity binding that uses a builder for constructing new instances.
 *
 * @author udoprog
 */
@Data
public class BuilderEntityBinding implements EntityBinding {
    public static final Converter<String, String> LOWER_TO_UPPER =
        CaseFormat.LOWER_CAMEL.converterTo(CaseFormat.UPPER_CAMEL);

    private final List<BuilderEntityFieldMapping> fields;
    private final Method newInstance;
    private final Method build;

    @Override
    public List<? extends EntityFieldMapping> fields() {
        return fields;
    }

    @Override
    public <Target> EntityTypeEncoder<Target, Object> newEntityTypeEncoder(
        final EntityResolver resolver, final EncodingFactory<Target> factory
    ) {
        final ImmutableList.Builder<BuilderEntityFieldEncoder<Target>> fieldsBuilder =
            ImmutableList.builder();

        for (final BuilderEntityFieldMapping field : fields) {
            fieldsBuilder.add(field
                .newEntityFieldEncoder(resolver, factory)
                .orElseThrow(() -> new IllegalArgumentException(
                    "Unable to apply encoding for field (" + field + ")")));
        }

        return new ReadFieldsEntityTypeEncoder<>(fieldsBuilder.build());
    }

    @Override
    public <Target> EntityTypeDecoder<Target, Object> newEntityTypeDecoder(
        final EntityResolver resolver, final EncodingFactory<Target> factory
    ) {
        final ImmutableList.Builder<BuilderEntityFieldDecoder<Target>> fieldsBuilder =
            ImmutableList.builder();

        for (final BuilderEntityFieldMapping field : fields) {
            fieldsBuilder.add(field
                .newEntityFieldDecoder(resolver, factory)
                .orElseThrow(() -> new IllegalArgumentException(
                    "Unable to apply encoding for field (" + field + ")")));
        }

        final List<BuilderEntityFieldDecoder<Target>> fields = fieldsBuilder.build();
        return new BuilderEntityTypeDecoder<>(fields, newInstance, build);
    }

    public static Stream<Match<EntityBinding>> detect(
        final EntityResolver resolver, final Type type
    ) {
        return Reflection
            .asClass(type)
            .flatMap(c -> Methods
                .of(c.getDeclaredMethods())
                .getMethods("builder")
                .filter(Reflection::isStatic)
                .map(newInstance -> {
                    final ImmutableList.Builder<BuilderEntityFieldMapping> fields =
                        ImmutableList.builder();

                    final Class<?> builderType = newInstance.getReturnType();
                    final Methods methods = Methods.of(builderType.getMethods());

                    final Method builderBuild = methods
                        .getMethods("build")
                        .findFirst()
                        .orElseThrow(() -> new IllegalArgumentException(
                            "Method build() missing on type (" + builderType + ")"));

                    if (!builderBuild.getGenericReturnType().equals(type)) {
                        throw new IllegalArgumentException(builderType +
                            " returns (" + builderBuild.getGenericReturnType() +
                            ") instead of expected (" + type + ")");
                    }

                    for (final java.lang.reflect.Field field : c.getDeclaredFields()) {
                        if ((field.getModifiers() & Modifier.STATIC) != 0) {
                            continue;
                        }

                        final Type propertyType = field.getGenericType();

                        final FieldReader reader = resolver
                            .detectFieldReader(type, field.getName(), Optional.of(propertyType))
                            .orElseThrow(() -> new IllegalArgumentException(
                                "Can't figure out how to read (" + type + ") field (" +
                                    field.getName() +
                                    ")"));

                        final Method setter = methods
                            .getMethods(field.getName(), propertyType)
                            .findFirst()
                            .orElseThrow(() -> new IllegalArgumentException(
                                "Builder does not have method " + builderType.getCanonicalName() +
                                    "#" +
                                    field.getName() + "(" + propertyType + ")"));

                        if (setter.getParameterTypes().length != 1) {
                            throw new IllegalArgumentException(
                                "Builder method (" + field.getName() +
                                    ") must take exactly one argument");
                        }

                        if (!propertyType.equals(setter.getGenericParameterTypes()[0])) {
                            throw new IllegalArgumentException(
                                "Builder parameter (" + setter.getParameterTypes()[0] +
                                    ") is not assignable to expected (" + propertyType + ")");
                        }

                        final Annotations annotations =
                            reader.annotations().merge(Annotations.of(field.getAnnotations()));

                        final String fieldName =
                            resolver.detectFieldName(type, annotations).orElseGet(field::getName);

                        final TypeMapping m = resolver.mapping(reader.fieldType(), annotations);
                        fields.add(new BuilderEntityFieldMapping(fieldName, m, reader, setter));
                    }

                    return new BuilderEntityBinding(fields.build(), newInstance, builderBuild);
                }))
            .map(Match.withPriority(Priority.LOW));
    }
}
