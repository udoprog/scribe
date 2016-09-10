package eu.toolchain.scribe;

import eu.toolchain.scribe.reflection.JavaType;
import lombok.Data;

import java.util.Optional;

/**
 * A type mapping associated with concrete classes.
 */
@Data
public class ConcreteClassMapping implements ClassMapping {
  private final JavaType type;
  private final Optional<String> typeName;

  /* left uninitialized to allow for circular dependencies */
  private ClassEncoding deferred;

  @Override
  public Optional<String> typeName() {
    return typeName;
  }

  @Override
  public <Target, EntityTarget> EntityEncoder<Target, EntityTarget, Object> newEntityTypeEncoder(
      final EntityResolver resolver, final EncoderFactory<Target, EntityTarget> factory
  ) {
    return deferred.newEntityEncoder(resolver, factory);
  }

  @Override
  public <Target> EntityStreamEncoder<Target, Object> newEntityTypeStreamEncoder(
      final EntityResolver resolver, final StreamEncoderFactory<Target> factory
  ) {
    return deferred.newEntityStreamEncoder(resolver, factory);
  }

  @Override
  public <Target, EntityTarget> EntityDecoder<Target, EntityTarget, Object> newEntityTypeDecoder(
      final EntityResolver resolver, final DecoderFactory<Target, EntityTarget> factory
  ) {
    return deferred.newEntityDecoder(resolver, factory);
  }

  @Override
  public void initialize(EntityResolver resolver) {
    this.deferred = resolver
        .detectEntityMapping(type)
        .orElseThrow(() -> new RuntimeException("Unable to detect binding for type: " + type));
  }
}
