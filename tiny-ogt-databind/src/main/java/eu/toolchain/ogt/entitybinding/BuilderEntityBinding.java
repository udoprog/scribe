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
import eu.toolchain.ogt.Priority;
import eu.toolchain.ogt.fieldreader.FieldReader;
import eu.toolchain.ogt.type.AccessibleType;
import eu.toolchain.ogt.type.JavaType;
import eu.toolchain.ogt.typemapping.TypeMapping;
import lombok.Data;

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
    private final JavaType.Method newInstance;
    private final JavaType.Method build;

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
        final EntityResolver resolver, final JavaType type
    ) {
        return type.getMethod("builder").filter(AccessibleType::isStatic).map(newInstance -> {
            final ImmutableList.Builder<BuilderEntityFieldMapping> fields = ImmutableList.builder();

            final JavaType returnType = newInstance.getReturnType();

            final JavaType.Method builderBuild = returnType
                .getMethod("build")
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                    "Method build() missing on type (" + returnType + ")"));

            if (!builderBuild.getReturnType().equals(type)) {
                throw new IllegalArgumentException(builderBuild +
                    " returns (" + builderBuild.getReturnType() +
                    ") instead of expected (" + type + ")");
            }

            type.getFields().filter(f -> !f.isStatic()).forEach(field -> {
                final JavaType propertyType = field.getFieldType();

                final FieldReader reader = resolver
                    .detectFieldReader(type, field.getName(), Optional.of(propertyType))
                    .orElseThrow(() -> new IllegalArgumentException(
                        "Can't figure out how to read (" + type + ") field (" +
                            field.getName() +
                            ")"));

                final JavaType.Method setter = returnType
                    .getMethod(field.getName(), propertyType)
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException(
                        "Builder does not have method " + returnType +
                            "#" +
                            field.getName() + "(" + propertyType + ")"));

                final Annotations annotations =
                    reader.annotations().merge(Annotations.of(field.getAnnotations()));

                final String fieldName =
                    resolver.detectFieldName(type, annotations).orElseGet(field::getName);

                final TypeMapping m = resolver.mapping(reader.fieldType(), annotations);
                fields.add(new BuilderEntityFieldMapping(fieldName, m, reader, setter));
            });

            return new BuilderEntityBinding(fields.build(), newInstance, builderBuild);
        }).map(Match.withPriority(Priority.LOW));
    }
}
