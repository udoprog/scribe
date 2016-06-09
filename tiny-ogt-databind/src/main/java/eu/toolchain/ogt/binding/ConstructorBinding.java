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
public class ConstructorBinding<T> implements SetEntityTypeBinding<T> {
    public static final Converter<String, String> LOWER_TO_UPPER =
        CaseFormat.LOWER_CAMEL.converterTo(CaseFormat.UPPER_CAMEL);
    public static final Joiner FIELD_JOINER = Joiner.on(", ");

    private final List<TypeFieldMapping> fields;
    private final InstanceBuilder instanceBuilder;

    @Override
    public List<? extends TypeFieldMapping> fields() {
        return fields;
    }

    @Override
    public Object decodeEntity(
        EntityDecoder<T> entityDecoder, TypeDecoder<T> decoder, Context path
    ) {
        final List<Object> arguments = new ArrayList<>();

        for (final TypeFieldMapping m : fields) {
            final Context p = path.push(m.name());

            Object argument = m
                .type()
                .fromOptional(entityDecoder.decodeField(m, p))
                .orElseThrow(() -> p.error("Missing required field (" + m.name() + ")"));

            arguments.add(argument);
        }

        try {
            return instanceBuilder.newInstance(arguments);
        } catch (final Exception e) {
            throw path.error("Could not build instance using " + instanceBuilder, e);
        }
    }

    @Override
    public String toString() {
        return FIELD_JOINER.join(fields);
    }

    public static Optional<Binding> detect(final EntityResolver resolver, final JavaType type) {
        return resolver.detectCreatorMethod(type).flatMap(creator -> {
            final ImmutableList.Builder<TypeFieldMapping> fields = ImmutableList.builder();

            for (final CreatorField field : creator.fields()) {
                final String fieldName = field
                    .name()
                    .orElseGet(() -> resolver
                        .detectPropertyName(type, field)
                        .orElseThrow(() -> new IllegalArgumentException(
                            "Cannot detect property name for field: " + field.toString())));

                final FieldReader reader = resolver
                    .detectFieldReader(type, fieldName, field.type())
                    .orElseThrow(() -> new IllegalArgumentException(
                        "Can't figure out how to read " + type + " field (" + fieldName + ")"));

                final Annotations annotations = field.annotations().merge(reader.annotations());
                final TypeMapping m = resolver.mapping(reader.fieldType(), annotations);

                fields.add(new TypeFieldMapping(fieldName, m, reader));
            }

            return Optional.of(new ConstructorBinding(fields.build(), creator.instanceBuilder()));
        });
    }
}
