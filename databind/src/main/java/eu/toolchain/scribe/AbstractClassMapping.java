package eu.toolchain.scribe;

import eu.toolchain.scribe.reflection.JavaType;
import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Data
public class AbstractClassMapping<Source> implements ClassMapping<Source> {
  public static final JavaType STRING = JavaType.of(String.class);
  public static final String DEFAULT_TYPE_FIELD = "type";

  private final JavaType type;
  private final Optional<String> typeName;
  private final List<SubType<Source>> subTypes;
  private final Optional<String> typeField;

  @Override
  public Optional<String> typeName() {
    return typeName;
  }

  @Override
  public <Target, EntityTarget> EntityEncoder<Target, EntityTarget, Source> newEntityTypeEncoder(
      final EntityResolver resolver, final EncoderFactory<Target, EntityTarget> factory
  ) {
    final Map<JavaType, AbstractEntityEncoder.TypeEntry<Target, EntityTarget, Source>> byType =
        new HashMap<>();

    for (final SubType<Source> subType : subTypes) {
      final ClassMapping<Source> m = subType.getMapping();

      final EntityEncoder<Target, EntityTarget, Source> encoding =
          m.newEntityTypeEncoder(resolver, factory);

      final String typeName = getTypeName(subType, m);

      byType.put(m.getType(), new AbstractEntityEncoder.TypeEntry<>(typeName, encoding));
    }

    final String fieldName = getTypeFieldName(resolver);

    final Encoder<Target, String> encoder = factory.<String>newEncoder(resolver, STRING)
        .findFirst()
        .orElseThrow(
            () -> new IllegalStateException("Could not find an encoder for the type field"));

    return new AbstractEntityEncoder<>(byType, factory,
        new TypeEntityFieldEncoder<>(fieldName, encoder));
  }

  @Override
  public <Target> EntityStreamEncoder<Target, Source> newEntityTypeStreamEncoder(
      final EntityResolver resolver, final StreamEncoderFactory<Target> factory
  ) {
    final Map<JavaType, AbstractEntityStreamEncoder.EntityEncoderEntry<Target, Source>> byType =
        new HashMap<>();

    for (final SubType<Source> subType : subTypes) {
      final ClassMapping<Source> m = subType.getMapping();

      final EntityStreamEncoder<Target, Source> encoder =
          m.newEntityTypeStreamEncoder(resolver, factory);

      final String typeName = getTypeName(subType, m);

      byType.put(m.getType(),
          new AbstractEntityStreamEncoder.EntityEncoderEntry<>(typeName, encoder));
    }

    final String fieldName = getTypeFieldName(resolver);

    final StreamEncoder<Target, String> encoder = factory.<String>newStreamEncoder(resolver, STRING)
        .findFirst()
        .orElseThrow(
            () -> new IllegalStateException("Could not find an encoder for the type field"));

    return new AbstractEntityStreamEncoder<>(byType, factory,
        new TypeEntityFieldStreamEncoder<>(fieldName, encoder));
  }

  @Override
  public <Target, EntityTarget> EntityDecoder<Target, EntityTarget, Source> newEntityTypeDecoder(
      final EntityResolver resolver, final DecoderFactory<Target, EntityTarget> factory
  ) {
    final Map<String, EntityDecoder<Target, EntityTarget, Source>> byName = new HashMap<>();

    for (final SubType<Source> subType : subTypes) {
      final ClassMapping<Source> m = subType.getMapping();

      final EntityDecoder<Target, EntityTarget, Source> encoding =
          m.newEntityTypeDecoder(resolver, factory);

      final String typeName = getTypeName(subType, m);
      byName.put(typeName, encoding);
    }

    final String fieldName = getTypeFieldName(resolver);

    final Decoder<Target, String> encoder = factory.<String>newDecoder(resolver, STRING)
        .findFirst()
        .orElseThrow(
            () -> new IllegalStateException("Could not find an encoder for the type field"));

    return new AbstractEntityDecoder<>(byName, factory,
        new TypeEntityFieldDecoder<>(fieldName, encoder));
  }

  private String getTypeFieldName(final EntityResolver resolver) {
    return typeField.orElseGet(() -> resolver
        .getOption(DatabindOptions.TypeFieldName.class)
        .map(DatabindOptions.TypeFieldName::getName)
        .orElse(DEFAULT_TYPE_FIELD));
  }

  private String getTypeName(
      final SubType<Source> subType, final ClassMapping<Source> m
  ) {
    return subType
        .getName()
        .orElseGet(() -> m
            .typeName()
            .orElseThrow(() -> new IllegalStateException(
                "No type name available for sub-type (" + subType + ")")));
  }
}
