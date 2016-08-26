package eu.toolchain.scribe.typemapping;

import eu.toolchain.scribe.DecoderFactory;
import eu.toolchain.scribe.EncoderFactory;
import eu.toolchain.scribe.EntityDecoder;
import eu.toolchain.scribe.EntityEncoder;
import eu.toolchain.scribe.EntityResolver;
import eu.toolchain.scribe.EntityStreamEncoder;
import eu.toolchain.scribe.JavaType;
import eu.toolchain.scribe.StreamEncoderFactory;
import eu.toolchain.scribe.entitymapping.EntityMapping;
import lombok.Data;

import java.util.Optional;

/**
 * A type mapping associated with concrete classes.
 */
@Data
public class ConcreteEntityTypeMapping implements EntityTypeMapping {
  private final JavaType type;
  private final Optional<String> typeName;

  /* left uninitialized to allow for circular dependencies */
  private EntityMapping mapping;

  @Override
  public Optional<String> typeName() {
    return typeName;
  }

  @Override
  public <Target> EntityEncoder<Target, Object> newEntityTypeEncoder(
      final EntityResolver resolver, final EncoderFactory<Target> factory
  ) {
    return mapping.newEntityTypeEncoder(resolver, factory);
  }

  @Override
  public <Target> EntityStreamEncoder<Target, Object> newEntityTypeStreamEncoder(
      final EntityResolver resolver, final StreamEncoderFactory<Target> factory
  ) {
    return mapping.newEntityTypeStreamEncoder(resolver, factory);
  }

  @Override
  public <Target> EntityDecoder<Target, Object> newEntityTypeDecoder(
      final EntityResolver resolver, final DecoderFactory<Target> factory
  ) {
    return mapping.newEntityTypeDecoder(resolver, factory);
  }

  @Override
  public void initialize(EntityResolver resolver) {
    this.mapping = resolver
        .detectEntityMapping(type)
        .orElseThrow(() -> new RuntimeException("Unable to detect binding for type: " + type));
  }
}
