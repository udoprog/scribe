package eu.toolchain.scribe;

import eu.toolchain.scribe.detector.ClassEncodingDetector;
import eu.toolchain.scribe.detector.Match;
import eu.toolchain.scribe.detector.MatchPriority;
import eu.toolchain.scribe.reflection.AccessibleType;
import eu.toolchain.scribe.reflection.Annotations;
import eu.toolchain.scribe.reflection.JavaType;
import lombok.Data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import static eu.toolchain.scribe.Streams.streamRequireOne;

/**
 * Entity binding that uses a builder for constructing new instances.
 *
 * @author udoprog
 */
@Data
public class BuilderClassEncoding<Source> implements ClassEncoding<Source> {
  private final List<BuilderEntityFieldMapping<Object>> fields;
  private final InstanceBuilder<Object> newInstance;
  private final JavaType.Method build;

  @Override
  public <Target, EntityTarget> EntityEncoder<Target, EntityTarget, Source> newEntityEncoder(
      final EntityResolver resolver, final EncoderFactory<Target, EntityTarget> factory
  ) {
    final ArrayList<ReadFieldsEntityEncoder.Field<Target, Object>> fields = new ArrayList<>();

    for (final BuilderEntityFieldMapping<Object> field : this.fields) {
      final BuilderEntityFieldEncoder<Target, Object> fieldEncoder =
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

    for (final BuilderEntityFieldMapping<Object> field : this.fields) {
      final BuilderEntityFieldStreamEncoder<Target, Object> encoder =
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
    final ArrayList<BuilderEntityFieldDecoder<Target, Object>> fields = new ArrayList<>();

    for (final BuilderEntityFieldMapping<Object> field : this.fields) {
      fields.add(streamRequireOne(field.newEntityFieldDecoder(resolver, factory)));
    }

    return new BuilderEntityDecoder<>(Collections.unmodifiableList(fields), newInstance, build,
        factory);
  }

  public static ClassEncodingDetector forStaticMethod(final String methodName) {
    return (resolver, type) -> {
      return type.getMethod("builder").filter(AccessibleType::isStatic).map(method -> {
        final InstanceBuilder<Object> instanceBuilder = InstanceBuilder.fromStaticMethod(method);
        return setupBuilderClassEncoding(resolver, type, instanceBuilder);
      }).map(Match.withPriority(MatchPriority.DEFAULT));
    };
  }

  public static ClassEncodingDetector forRelatedClass(
      final Function<JavaType, Optional<JavaType>> builderTypeResolver
  ) {
    return (resolver, type) -> {
      return builderTypeResolver
          .apply(type)
          .map(Stream::of)
          .orElseGet(Stream::empty)
          .map(builderClass -> {
            final JavaType.Constructor builderClassConstructor = builderClass
                .getConstructor()
                .orElseThrow(() -> new IllegalArgumentException(
                    "Builder class (" + builderClass + ") does not have an empty constructor"));

            final InstanceBuilder.Constructor<Object> instanceBuilder =
                InstanceBuilder.fromConstructor(builderClassConstructor);

            return setupBuilderClassEncoding(resolver, type, instanceBuilder);
          })
          .map(Match.withPriority(MatchPriority.HIGH));
    };
  }

  private static BuilderClassEncoding<Object> setupBuilderClassEncoding(
      final EntityResolver resolver, final JavaType type,
      final InstanceBuilder<Object> instanceBuilder
  ) {
    final ArrayList<BuilderEntityFieldMapping<Object>> fields = new ArrayList<>();

    final JavaType builderType = instanceBuilder.getInstanceType();

    final JavaType.Method builderBuild = builderType
        .getMethod("build")
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException(
            "Method build() missing on type (" + builderType + ")"));

    if (!builderBuild.getReturnType().equals(type)) {
      throw new IllegalArgumentException(
          builderBuild + " returns (" + builderBuild.getReturnType() +
              ") instead forAnnotation expected (" + type + ")");
    }

    resolver.detectFields(type).forEach(field -> {
      final JavaType fieldType = field.getType();
      final String serializedName = field.getSerializedName();

      final FieldReader reader = resolver
          .detectFieldReader(type, serializedName, fieldType)
          .orElseThrow(() -> new IllegalArgumentException(
              "Can't figure out how to read (" + type + ") field (" + serializedName + ")"));

      final JavaType.Method setter = builderType
          .getMethod(field.getFieldName(), fieldType)
          .findFirst()
          .orElseThrow(() -> new IllegalArgumentException(
              "Builder does not have method " + builderType + "#" + serializedName + "(" +
                  fieldType + ")"));

      final Annotations annotations = reader.annotations().merge(field.getAnnotations());

      final Mapping<Object> m = resolver.mapping(reader.fieldType(), annotations);
      final Flags flags = resolver.detectFieldFlags(reader.fieldType(), annotations);
      fields.add(new BuilderEntityFieldMapping<>(serializedName, m, reader, setter, flags));
    });

    return new BuilderClassEncoding<>(Collections.unmodifiableList(fields), instanceBuilder,
        builderBuild);
  }
}
