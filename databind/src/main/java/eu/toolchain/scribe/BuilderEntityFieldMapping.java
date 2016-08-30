package eu.toolchain.scribe;

import eu.toolchain.scribe.reflection.JavaType;
import lombok.Data;

import java.util.Optional;

@Data
class BuilderEntityFieldMapping implements EntityFieldMapping {
  private final String name;
  private final Mapping mapping;
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
