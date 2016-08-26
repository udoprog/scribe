package eu.toolchain.scribe.entitymapping;

import eu.toolchain.scribe.DecoderFactory;
import eu.toolchain.scribe.EncoderFactory;
import eu.toolchain.scribe.EntityResolver;
import eu.toolchain.scribe.StreamEncoderFactory;
import eu.toolchain.scribe.fieldreader.FieldReader;
import eu.toolchain.scribe.typemapping.TypeMapping;
import lombok.Data;

import java.util.Optional;

@Data
public class DefaultEntityFieldMapping implements EntityFieldMapping {
  private final String name;
  private final TypeMapping mapping;
  private final FieldReader reader;

  @Override
  public <Target> Optional<EntityFieldEncoder<Target, Object>> newEntityFieldEncoder(
      final EntityResolver resolver, final EncoderFactory<Target> factory
  ) {
    return mapping
        .newEncoder(resolver, factory)
        .map(parent -> new DefaultEntityFieldEncoder<>(parent, name, reader));
  }

  @Override
  public <Target> Optional<EntityFieldStreamEncoder<Target, Object>> newEntityFieldStreamEncoder(
      final EntityResolver resolver, final StreamEncoderFactory<Target> factory
  ) {
    return mapping
        .newStreamEncoder(resolver, factory)
        .map(parent -> new DefaultEntityFieldStreamEncoder<>(parent, name, reader));
  }

  @Override
  public <T> Optional<? extends EntityFieldDecoder<T, Object>> newEntityFieldDecoder(
      final EntityResolver resolver, final DecoderFactory<T> factory
  ) {
    return mapping
        .newDecoder(resolver, factory)
        .map(parent -> new DefaultEntityFieldDecoder<>(parent, name));
  }
}
