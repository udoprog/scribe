package eu.toolchain.ogt.binding;

import com.google.common.base.CaseFormat;
import com.google.common.base.Converter;
import com.google.common.collect.ImmutableList;
import eu.toolchain.ogt.Annotations;
import eu.toolchain.ogt.Context;
import eu.toolchain.ogt.EntityDecoder;
import eu.toolchain.ogt.EntityResolver;
import eu.toolchain.ogt.JavaType;
import eu.toolchain.ogt.TypeDecoder;
import eu.toolchain.ogt.creatormethod.CreatorField;
import eu.toolchain.ogt.creatormethod.InstanceBuilder;
import eu.toolchain.ogt.fieldreader.FieldReader;
import eu.toolchain.ogt.type.TypeMapping;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Type binding implementation that uses builder methods for constructing instances.
 *
 * @author udoprog
 */
@Data
public class ConstructorEntityBinding implements ReadFieldsEntityBinding {
    public static final Converter<String, String> LOWER_TO_UPPER =
        CaseFormat.LOWER_CAMEL.converterTo(CaseFormat.UPPER_CAMEL);

    private final List<FieldMapping> fields;
    private final InstanceBuilder instanceBuilder;

    @Override
    public List<FieldMapping> fields() {
        return fields;
    }

    @Override
    public <T> Object decodeEntity(
        EntityDecoder<T> entityDecoder, TypeDecoder<T> decoder, Context path
    ) {
        final List<Object> arguments = new ArrayList<>();

        for (final FieldMapping m : fields) {
            final Context p = path.push(m.name());

            arguments.add(m
                .type()
                .fromOptional(entityDecoder.decodeField(m, p))
                .orElseThrow(() -> p.error("Missing required field (" + m.name() + ")")));
        }

        try {
            return instanceBuilder.newInstance(arguments);
        } catch (final Exception e) {
            throw path.error("Could not build instance using " + instanceBuilder, e);
        }
    }

    public static Optional<EntityBinding> detect(
        final EntityResolver resolver, final JavaType type
    ) {
        return resolver.detectCreatorMethod(type).flatMap(creator -> {
            final ImmutableList.Builder<FieldMapping> fields = ImmutableList.builder();

            for (final CreatorField field : creator.fields()) {
                final String fieldGetter = resolver
                    .detectFieldName(type, field)
                    .orElseGet(() -> field
                        .getName()
                        .orElseThrow(() -> new IllegalArgumentException(
                            "Cannot detect property name for field: " + field.toString())));

                final FieldReader reader = resolver
                    .detectFieldReader(type, fieldGetter, field.getType())
                    .orElseThrow(() -> new IllegalArgumentException(
                        "Can't figure out how to read " + type + " field (" + fieldGetter + ")"));

                final Annotations annotations = field.getAnnotations().merge(reader.annotations());
                final TypeMapping m = resolver.mapping(reader.fieldType(), annotations);

                fields.add(new ConstructorEntityFieldMapping(fieldGetter, m, reader));
            }

            return Optional.of(
                new ConstructorEntityBinding(fields.build(), creator.instanceBuilder()));
        });
    }

    @Data
    public static class ConstructorEntityFieldMapping implements FieldMapping {
        private final String name;
        private final TypeMapping mapping;
        private final FieldReader reader;

        @Override
        public String name() {
            return name;
        }

        @Override
        public TypeMapping type() {
            return mapping;
        }

        public FieldReader reader() {
            return reader;
        }
    }
}
