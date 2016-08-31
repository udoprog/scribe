package eu.toolchain.scribe;

import eu.toolchain.scribe.detector.Match;
import eu.toolchain.scribe.detector.MatchPriority;
import eu.toolchain.scribe.reflection.AccessibleType;
import eu.toolchain.scribe.reflection.Annotations;
import eu.toolchain.scribe.reflection.JavaType;
import lombok.Data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

/**
 * Entity binding that uses a builder for constructing new instances.
 *
 * @author udoprog
 */
@Data
public class BuilderClassEncoding implements ClassEncoding {
  private final List<BuilderEntityFieldMapping> fields;
  private final JavaType.Method newInstance;
  private final JavaType.Method build;

  @Override
  public <Target> EntityEncoder<Target, Object> newEntityEncoder(
      final EntityResolver resolver, final EncoderFactory<Target> factory
  ) {
    final ArrayList<ReadFieldsEntityEncoder.Field<Target, Object>> fields = new ArrayList<>();

    for (final BuilderEntityFieldMapping field : this.fields) {
      final BuilderEntityFieldEncoder<Target> fieldEncoder = field
          .newEntityFieldEncoder(resolver, factory)
          .orElseThrow(() -> new IllegalArgumentException(
              "Unable to apply encoding for field (" + field + ")"));

      fields.add(new ReadFieldsEntityEncoder.Field<>(fieldEncoder, field.getReader()));
    }

    return new ReadFieldsEntityEncoder<>(Collections.unmodifiableList(fields), factory);
  }

  @Override
  public <Target> EntityStreamEncoder<Target, Object> newEntityStreamEncoder(
      final EntityResolver resolver, final StreamEncoderFactory<Target> factory
  ) {
    final ArrayList<ReadFieldsEntityStreamEncoder.ReadFieldsEntityField<Target, Object>> fields =
        new ArrayList<>();

    for (final BuilderEntityFieldMapping field : this.fields) {
      final BuilderEntityFieldStreamEncoder<Target> encoder = field
          .newEntityFieldStreamEncoder(resolver, factory)
          .orElseThrow(() -> new IllegalArgumentException(
              "Unable to apply encoding for field (" + field + ")"));

      fields.add(
          new ReadFieldsEntityStreamEncoder.ReadFieldsEntityField<>(encoder, field.getReader()));
    }

    return new ReadFieldsEntityStreamEncoder<>(Collections.unmodifiableList(fields), factory);
  }

  @Override
  public <Target> EntityDecoder<Target, Object> newEntityDecoder(
      final EntityResolver resolver, final DecoderFactory<Target> factory
  ) {
    final ArrayList<BuilderEntityFieldDecoder<Target>> fields = new ArrayList<>();

    for (final BuilderEntityFieldMapping field : this.fields) {
      fields.add(field
          .newEntityFieldDecoder(resolver, factory)
          .orElseThrow(() -> new IllegalArgumentException(
              "Unable to apply encoding for field (" + field + ")")));
    }

    return new BuilderEntityDecoder<>(Collections.unmodifiableList(fields), newInstance, build,
        factory);
  }

  public static Stream<Match<ClassEncoding>> detect(
      final EntityResolver resolver, final JavaType type
  ) {
    return type.getMethod("builder").filter(AccessibleType::isStatic).map(newInstance -> {
      final ArrayList<BuilderEntityFieldMapping> fields = new ArrayList<>();

      final JavaType returnType = newInstance.getReturnType();

      final JavaType.Method builderBuild = returnType
          .getMethod("build")
          .findFirst()
          .orElseThrow(() -> new IllegalArgumentException(
              "Method build() missing on type (" + returnType + ")"));

      if (!builderBuild.getReturnType().equals(type)) {
        throw new IllegalArgumentException(
            builderBuild + " returns (" + builderBuild.getReturnType() +
                ") instead forAnnotation expected (" + type + ")");
      }

      type.getFields().filter(f -> !f.isStatic()).forEach(field -> {
        final JavaType propertyType = field.getFieldType();

        final FieldReader reader = resolver
            .detectFieldReader(type, field.getName(), propertyType)
            .orElseThrow(() -> new IllegalArgumentException(
                "Can't figure out how to read (" + type + ") field (" + field.getName() + ")"));

        final JavaType.Method setter = returnType
            .getMethod(field.getName(), propertyType)
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException(
                "Builder does not have method " + returnType + "#" + field.getName() + "(" +
                    propertyType + ")"));

        final Annotations annotations =
            reader.annotations().merge(Annotations.of(field.getAnnotationStream()));

        final String fieldName =
            resolver.detectFieldName(type, annotations).orElseGet(field::getName);

        final Mapping m = resolver.mapping(reader.fieldType(), annotations);
        final Flags flags = resolver.detectFieldFlags(reader.fieldType(), annotations);
        fields.add(new BuilderEntityFieldMapping(fieldName, m, reader, setter, flags));
      });

      return new BuilderClassEncoding(Collections.unmodifiableList(fields), newInstance,
          builderBuild);
    }).map(Match.withPriority(MatchPriority.LOW));
  }
}