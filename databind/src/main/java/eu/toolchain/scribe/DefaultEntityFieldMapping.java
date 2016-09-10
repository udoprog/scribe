package eu.toolchain.scribe;

import lombok.Data;

import java.util.stream.Stream;

@Data
public class DefaultEntityFieldMapping implements EntityFieldMapping {
  private final String name;
  private final Mapping mapping;
  private final FieldReader reader;
  private final Flags flags;

  @Override
  public <Target, EntityTarget> Stream<EntityFieldEncoder<Target, Object>> newEntityFieldEncoder(
      final EntityResolver resolver, final EncoderFactory<Target, EntityTarget> factory
  ) {
    return mapping
        .newEncoder(resolver, factory, flags)
        .map(parent -> new DefaultEntityFieldEncoder<>(name, reader, flags, parent));
  }

  @Override
  public <Target> Stream<EntityFieldStreamEncoder<Target, Object>> newEntityFieldStreamEncoder(
      final EntityResolver resolver, final StreamEncoderFactory<Target> factory
  ) {
    return mapping
        .newStreamEncoder(resolver, factory, flags)
        .map(parent -> new DefaultEntityFieldStreamEncoder<>(parent, name, reader));
  }

  @Override
  public <Target, EntityTarget> Stream<? extends EntityFieldDecoder<Target, Object>>
  newEntityFieldDecoder(
      final EntityResolver resolver, final DecoderFactory<Target, EntityTarget> factory
  ) {
    return mapping
        .newDecoder(resolver, factory, flags)
        .map(parent -> new DefaultEntityFieldDecoder<>(name, flags, parent));
  }
}
