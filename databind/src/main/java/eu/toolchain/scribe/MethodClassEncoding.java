package eu.toolchain.scribe;

import eu.toolchain.scribe.detector.Match;
import eu.toolchain.scribe.detector.MatchPriority;
import eu.toolchain.scribe.reflection.Annotations;
import eu.toolchain.scribe.reflection.JavaType;
import lombok.Data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static eu.toolchain.scribe.Streams.streamRequireOne;

@Data
public class MethodClassEncoding<Source> implements ClassEncoding<Source> {
  private final List<DefaultEntityFieldMapping<Object>> fields;
  private final ClassInstanceBuilder<Source> classInstanceBuilder;

  @Override
  public <Target, EntityTarget> EntityEncoder<Target, EntityTarget, Source> newEntityEncoder(
      final EntityResolver resolver, final EncoderFactory<Target, EntityTarget> factory
  ) {
    final ArrayList<ReadFieldsEntityEncoder.Field<Target, Object>> fields = new ArrayList<>();

    for (final DefaultEntityFieldMapping<Object> field : this.fields) {
      final EntityFieldEncoder<Target, Object> fieldEncoder =
          streamRequireOne(field.newEntityFieldEncoder(resolver, factory));

      fields.add(new ReadFieldsEntityEncoder.Field<>(fieldEncoder, field.getReader()));
    }

    return new ReadFieldsEntityEncoder<>(Collections.unmodifiableList(fields), factory);
  }

  @Override
  public <Target> EntityStreamEncoder<Target, Source> newEntityStreamEncoder(
      final EntityResolver resolver, final StreamEncoderFactory<Target> factory
  ) {
    final ArrayList<ReadFieldsEntityStreamEncoder.ReadFieldsEntityField<Target, Object>> fields =
        new ArrayList<>();

    for (final DefaultEntityFieldMapping<Object> field : this.fields) {
      final EntityFieldStreamEncoder<Target, Object> encoder =
          streamRequireOne(field.newEntityFieldStreamEncoder(resolver, factory));

      fields.add(
          new ReadFieldsEntityStreamEncoder.ReadFieldsEntityField<>(encoder, field.getReader()));
    }

    return new ReadFieldsEntityStreamEncoder<>(Collections.unmodifiableList(fields), factory);
  }

  @Override
  public <Target, EntityTarget> EntityDecoder<Target, EntityTarget, Source> newEntityDecoder(
      final EntityResolver resolver, final DecoderFactory<Target, EntityTarget> factory
  ) {
    final ArrayList<EntityFieldDecoder<Target, Object>> fields = new ArrayList<>();

    for (final EntityFieldMapping<Object> field : this.fields) {
      fields.add(streamRequireOne(field.newEntityFieldDecoder(resolver, factory)));
    }

    return new DefaultEntityDecoder<>(Collections.unmodifiableList(fields), classInstanceBuilder,
        factory);
  }

  public static Stream<Match<ClassEncoding<Object>>> detect(
      final EntityResolver resolver, final JavaType type
  ) {
    return resolver.detectInstanceBuilder(type).map(creator -> {
      final ArrayList<DefaultEntityFieldMapping<Object>> fields = new ArrayList<>();

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

        final Annotations base = field.getAnnotations().merge(reader.annotations());

        final Annotations annotations;

        if (!field.isImmediate()) {
          annotations = base.merge(resolver.detectImmediateAnnotations(type, fieldName));
        } else {
          annotations = base;
        }

        final Mapping<Object> m = resolver.mapping(reader.fieldType(), annotations);
        final Flags flags = resolver.detectFieldFlags(reader.fieldType(), annotations);

        fields.add(new DefaultEntityFieldMapping<>(fieldName, m, reader, flags));
      }

      return Stream.of(new MethodClassEncoding<>(Collections.unmodifiableList(fields), creator));
    }).orElseGet(Stream::empty).map(Match.withPriority(MatchPriority.HIGH));
  }
}
