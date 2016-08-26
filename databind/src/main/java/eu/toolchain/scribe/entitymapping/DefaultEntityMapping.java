package eu.toolchain.scribe.entitymapping;

import eu.toolchain.scribe.Annotations;
import eu.toolchain.scribe.DecoderFactory;
import eu.toolchain.scribe.EncoderFactory;
import eu.toolchain.scribe.EntityDecoder;
import eu.toolchain.scribe.EntityEncoder;
import eu.toolchain.scribe.EntityField;
import eu.toolchain.scribe.EntityResolver;
import eu.toolchain.scribe.EntityStreamEncoder;
import eu.toolchain.scribe.JavaType;
import eu.toolchain.scribe.Match;
import eu.toolchain.scribe.MatchPriority;
import eu.toolchain.scribe.StreamEncoderFactory;
import eu.toolchain.scribe.creatormethod.InstanceBuilder;
import eu.toolchain.scribe.fieldreader.FieldReader;
import eu.toolchain.scribe.typemapping.TypeMapping;
import lombok.Data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

@Data
public class DefaultEntityMapping implements EntityMapping {
  private final List<DefaultEntityFieldMapping> fields;
  private final InstanceBuilder instanceBuilder;

  @Override
  public List<? extends EntityFieldMapping> fields() {
    return fields;
  }

  @Override
  public <Target> EntityEncoder<Target, Object> newEntityTypeEncoder(
      final EntityResolver resolver, final EncoderFactory<Target> factory
  ) {
    final ArrayList<ReadFieldsEntityEncoder.ReadFieldsEntityField<Target, Object>> fields =
        new ArrayList<>();

    for (final DefaultEntityFieldMapping field : this.fields) {
      final EntityFieldEncoder<Target, Object> fieldEncoder = field
          .newEntityFieldEncoder(resolver, factory)
          .orElseThrow(() -> new IllegalArgumentException(
              "Unable to apply encoding for field (" + field + ")"));

      fields.add(
          new ReadFieldsEntityEncoder.ReadFieldsEntityField<>(fieldEncoder, field.getReader()));
    }

    return new ReadFieldsEntityEncoder<>(Collections.unmodifiableList(fields), factory);
  }

  @Override
  public <Target> EntityStreamEncoder<Target, Object> newEntityTypeStreamEncoder(
      final EntityResolver resolver, final StreamEncoderFactory<Target> factory
  ) {
    final ArrayList<ReadFieldsEntityStreamEncoder.ReadFieldsEntityField<Target, Object>> fields =
        new ArrayList<>();

    for (final DefaultEntityFieldMapping field : this.fields) {
      final EntityFieldStreamEncoder<Target, Object> encoder = field
          .newEntityFieldStreamEncoder(resolver, factory)
          .orElseThrow(() -> new IllegalArgumentException(
              "Unable to apply encoding for field (" + field + ")"));

      fields.add(
          new ReadFieldsEntityStreamEncoder.ReadFieldsEntityField<>(encoder, field.getReader()));
    }

    return new ReadFieldsEntityStreamEncoder<>(Collections.unmodifiableList(fields), factory);
  }

  @Override
  public <Target> EntityDecoder<Target, Object> newEntityTypeDecoder(
      final EntityResolver resolver, final DecoderFactory<Target> factory
  ) {
    final ArrayList<EntityFieldDecoder<Target, Object>> fields = new ArrayList<>();

    for (final EntityFieldMapping field : this.fields) {
      fields.add(field
          .newEntityFieldDecoder(resolver, factory)
          .orElseThrow(() -> new IllegalArgumentException(
              "Unable to apply encoding for field (" + field + ")")));
    }

    return new DefaultEntityDecoder<>(Collections.unmodifiableList(fields), instanceBuilder,
        factory);
  }

  public static Stream<Match<EntityMapping>> detect(
      final EntityResolver resolver, final JavaType type
  ) {
    return resolver.detectCreatorMethod(type).map(creator -> {
      final ArrayList<DefaultEntityFieldMapping> fields = new ArrayList<>();

      for (final EntityField field : creator.getFields()) {
        final String fieldName = field
            .getName()
            .orElseGet(() -> creator
                .getFieldNames()
                .map(names -> names.get(field.getIndex()))
                .orElseThrow(() -> new IllegalArgumentException(
                    "Cannot detect property name for field: " + field.toString())));

        final FieldReader reader = resolver
            .detectFieldReader(type, fieldName, field.getType())
            .orElseThrow(() -> new IllegalArgumentException(
                "Can't figure out how to read " + type + " field (" + fieldName + ")"));

        final Annotations annotations = field.getAnnotations().merge(reader.annotations());
        final TypeMapping m = resolver.mapping(reader.fieldType(), annotations);

        fields.add(new DefaultEntityFieldMapping(fieldName, m, reader));
      }

      return Stream.of(new DefaultEntityMapping(Collections.unmodifiableList(fields), creator));
    }).orElseGet(Stream::empty).map(Match.withPriority(MatchPriority.HIGH));
  }
}
