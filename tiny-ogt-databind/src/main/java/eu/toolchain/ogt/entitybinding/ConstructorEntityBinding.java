package eu.toolchain.ogt.entitybinding;

import com.google.common.base.CaseFormat;
import com.google.common.base.Converter;
import com.google.common.collect.ImmutableList;
import eu.toolchain.ogt.Annotations;
import eu.toolchain.ogt.EncodingFactory;
import eu.toolchain.ogt.EntityField;
import eu.toolchain.ogt.EntityResolver;
import eu.toolchain.ogt.EntityTypeDecoder;
import eu.toolchain.ogt.EntityTypeEncoder;
import eu.toolchain.ogt.Match;
import eu.toolchain.ogt.Priority;
import eu.toolchain.ogt.creatormethod.InstanceBuilder;
import eu.toolchain.ogt.fieldreader.FieldReader;
import eu.toolchain.ogt.typemapping.TypeMapping;
import lombok.Data;

import java.lang.reflect.Type;
import java.util.List;
import java.util.stream.Stream;

/**
 * Entity binding implementation that uses a constructor to build a new instance.
 *
 * @author udoprog
 */
@Data
public class ConstructorEntityBinding implements EntityBinding {
    public static final Converter<String, String> LOWER_TO_UPPER =
        CaseFormat.LOWER_CAMEL.converterTo(CaseFormat.UPPER_CAMEL);

    private final List<EntityFieldMapping> fields;
    private final InstanceBuilder instanceBuilder;

    @Override
    public List<EntityFieldMapping> fields() {
        return fields;
    }

    @Override
    public <Target> EntityTypeEncoder<Target, Object> newEntityTypeEncoder(
        final EntityResolver resolver, final EncodingFactory<Target> factory
    ) {
        final ImmutableList.Builder<EntityFieldEncoder<Target, Object>> fieldsBuilder =
            ImmutableList.builder();

        for (final EntityFieldMapping field : fields) {
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
        final ImmutableList.Builder<EntityFieldDecoder<Target, Object>> fieldsBuilder =
            ImmutableList.builder();

        for (final EntityFieldMapping field : fields) {
            fieldsBuilder.add(field
                .newEntityFieldDecoder(resolver, factory)
                .orElseThrow(() -> new IllegalArgumentException(
                    "Unable to apply encoding for field (" + field + ")")));
        }

        final List<EntityFieldDecoder<Target, Object>> fields = fieldsBuilder.build();
        return new ConstructorEntityTypeDecoder<>(fields, instanceBuilder);
    }

    public static Stream<Match<EntityBinding>> detect(
        final EntityResolver resolver, final Type type
    ) {
        return resolver.detectCreatorMethod(type).map(creator -> {
            final ImmutableList.Builder<EntityFieldMapping> fields = ImmutableList.builder();

            for (final EntityField field : creator.fields()) {
                final String fieldName = resolver
                    .detectFieldName(type, field.getAnnotations())
                    .orElseGet(() -> field
                        .getName()
                        .orElseThrow(() -> new IllegalArgumentException(
                            "Cannot detect property name for field: " + field.toString())));

                final FieldReader reader = resolver
                    .detectFieldReader(type, fieldName, field.getType())
                    .orElseThrow(() -> new IllegalArgumentException(
                        "Can't figure out how to read " + type + " field (" + fieldName + ")"));

                final Annotations annotations = field.getAnnotations().merge(reader.annotations());
                final TypeMapping m = resolver.mapping(reader.fieldType(), annotations);

                fields.add(new ConstructorEntityFieldMapping(fieldName, m, reader));
            }

            return Stream.of(
                new ConstructorEntityBinding(fields.build(), creator.instanceBuilder()));
        }).orElseGet(Stream::empty).map(Match.withPriority(Priority.HIGH));
    }
}
