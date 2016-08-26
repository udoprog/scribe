package eu.toolchain.scribe.typemapping;

import eu.toolchain.scribe.DatabindOptions;
import eu.toolchain.scribe.Decoder;
import eu.toolchain.scribe.DecoderFactory;
import eu.toolchain.scribe.Encoder;
import eu.toolchain.scribe.EncoderFactory;
import eu.toolchain.scribe.EntityDecoder;
import eu.toolchain.scribe.EntityEncoder;
import eu.toolchain.scribe.EntityResolver;
import eu.toolchain.scribe.EntityStreamEncoder;
import eu.toolchain.scribe.JavaType;
import eu.toolchain.scribe.StreamEncoder;
import eu.toolchain.scribe.StreamEncoderFactory;
import eu.toolchain.scribe.entitymapper.SubType;
import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Data
public class PropertyAbstractEntityTypeMapping implements EntityTypeMapping {
  public static final JavaType STRING = JavaType.of(String.class);
  public static final String DEFAULT_TYPE_FIELD = "type";

  private final JavaType type;
  private final Optional<String> typeName;
  private final List<SubType> subTypes;
  private final Optional<String> typeField;

  @Override
  public Optional<String> typeName() {
    return typeName;
  }

  @Override
  public <Target> EntityEncoder<Target, Object> newEntityTypeEncoder(
      final EntityResolver resolver, final EncoderFactory<Target> factory
  ) {
    final Map<JavaType, AbstractEntityEncoder.EntityEncoderEntry<Target>> byType = new HashMap<>();

    for (final SubType subType : subTypes) {
      final EntityTypeMapping m = subType.getMapping();

      final EntityEncoder<Target, Object> encoding = m.newEntityTypeEncoder(resolver, factory);

      final String typeName = getTypeName(subType, m);

      byType.put(m.getType(), new AbstractEntityEncoder.EntityEncoderEntry<>(typeName, encoding));
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
  public <Target> EntityStreamEncoder<Target, Object> newEntityTypeStreamEncoder(
      final EntityResolver resolver, final StreamEncoderFactory<Target> factory
  ) {
    final Map<JavaType, AbstractEntityStreamEncoder.EntityEncoderEntry<Target>> byType =
        new HashMap<>();

    for (final SubType subType : subTypes) {
      final EntityTypeMapping m = subType.getMapping();

      final EntityStreamEncoder<Target, Object> encoder =
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
  public <Target> EntityDecoder<Target, Object> newEntityTypeDecoder(
      final EntityResolver resolver, final DecoderFactory<Target> factory
  ) {
    final Map<String, EntityDecoder<Target, Object>> byName = new HashMap<>();

    for (final SubType subType : subTypes) {
      final EntityTypeMapping m = subType.getMapping();

      final EntityDecoder<Target, Object> encoding = m.newEntityTypeDecoder(resolver, factory);

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

  private String getTypeName(final SubType subType, final EntityTypeMapping m) {
    return subType
        .getName()
        .orElseGet(() -> m
            .typeName()
            .orElseThrow(() -> new IllegalStateException(
                "No type name available for sub-type (" + subType + ")")));
  }
}
