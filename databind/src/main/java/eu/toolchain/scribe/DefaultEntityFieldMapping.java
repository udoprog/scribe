package eu.toolchain.scribe;

import lombok.Data;

import java.util.stream.Stream;

@Data
public class DefaultEntityFieldMapping<Source> implements EntityFieldMapping<Source> {
  private final String name;
  private final Mapping<Source> mapping;
  private final FieldReader reader;
  private final Flags flags;

  @Override
  public <Target, EntityTarget> Stream<EntityFieldEncoder<Target, Source>> newEntityFieldEncoder(
      final EntityResolver resolver, final EncoderFactory<Target, EntityTarget> factory
  ) {
    return mapping
        .newEncoder(resolver, factory, flags)
        .map(parent -> new DefaultEntityFieldEncoder<>(name, reader, flags, parent));
  }

  @Override
  public <Target> Stream<EntityFieldStreamEncoder<Target, Source>> newEntityFieldStreamEncoder(
      final EntityResolver resolver, final StreamEncoderFactory<Target> factory
  ) {
    return mapping
        .newStreamEncoder(resolver, factory, flags)
        .map(parent -> new DefaultEntityFieldStreamEncoder<>(parent, name, reader));
  }

  @Override
  public <Target, EntityTarget> Stream<? extends EntityFieldDecoder<Target, Source>>
  newEntityFieldDecoder(
      final EntityResolver resolver, final DecoderFactory<Target, EntityTarget> factory
  ) {
    return mapping
        .newDecoder(resolver, factory, flags)
        .map(parent -> new DefaultEntityFieldDecoder<>(name, flags, parent));
  }
}
