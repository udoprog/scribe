package eu.toolchain.scribe.entitymapping;

import eu.toolchain.scribe.DecoderFactory;
import eu.toolchain.scribe.EncoderFactory;
import eu.toolchain.scribe.EntityResolver;
import eu.toolchain.scribe.Flags;
import eu.toolchain.scribe.JavaType;
import eu.toolchain.scribe.StreamEncoderFactory;
import eu.toolchain.scribe.fieldreader.FieldReader;
import eu.toolchain.scribe.typemapping.TypeMapping;
import lombok.Data;

import java.util.Optional;

@Data
class BuilderEntityFieldMapping implements EntityFieldMapping {
  private final String name;
  private final TypeMapping mapping;
  private final FieldReader reader;
  private final JavaType.Method setter;
  private final Flags flags;

  @Override
  public <Target> Optional<BuilderEntityFieldEncoder<Target>> newEntityFieldEncoder(
      final EntityResolver resolver, final EncoderFactory<Target> factory
  ) {
    return mapping
        .newEncoder(resolver, flags, factory)
        .map(parent -> new BuilderEntityFieldEncoder<>(name, reader, mapping, setter, flags,
            parent));
  }

  @Override
  public <Target> Optional<BuilderEntityFieldStreamEncoder<Target>> newEntityFieldStreamEncoder(
      final EntityResolver resolver, final StreamEncoderFactory<Target> factory
  ) {
    return mapping
        .newStreamEncoder(resolver, flags, factory)
        .map(
            parent -> new BuilderEntityFieldStreamEncoder<>(name, reader, mapping, setter, parent));
  }

  @Override
  public <Target> Optional<BuilderEntityFieldDecoder<Target>> newEntityFieldDecoder(
      final EntityResolver resolver, final DecoderFactory<Target> factory
  ) {
    return mapping
        .newDecoder(resolver, flags, factory)
        .map(parent -> new BuilderEntityFieldDecoder<>(name, reader, mapping, setter, flags,
            parent));
  }
}
