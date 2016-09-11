package eu.toolchain.scribe;

import eu.toolchain.scribe.reflection.JavaType;
import lombok.Data;

import java.util.List;
import java.util.Optional;

/**
 * A type mapping associated with concrete classes.
 */
@Data
public class DatabindClassMapping<Source> implements ClassMapping<Source> {
  private final JavaType type;
  private final Optional<String> typeName;

  /* left uninitialized to allow for circular dependencies */
  private ClassEncoding<Source> deferred;

  @Override
  public Optional<String> typeName() {
    return typeName;
  }

  @Override
  public <Target, EntityTarget> EntityEncoder<Target, EntityTarget, Source> newEntityTypeEncoder(
      final EntityResolver resolver, final EncoderFactory<Target, EntityTarget> factory
  ) {
    return deferred.newEntityEncoder(resolver, factory);
  }

  @Override
  public <Target> EntityStreamEncoder<Target, Source> newEntityTypeStreamEncoder(
      final EntityResolver resolver, final StreamEncoderFactory<Target> factory
  ) {
    return deferred.newEntityStreamEncoder(resolver, factory);
  }

  @Override
  public <Target, EntityTarget> EntityDecoder<Target, EntityTarget, Source> newEntityTypeDecoder(
      final EntityResolver resolver, final DecoderFactory<Target, EntityTarget> factory
  ) {
    return deferred.newEntityDecoder(resolver, factory);
  }

  @SuppressWarnings("unchecked")
  @Override
  public void postCacheInitialize(final EntityResolver resolver) {
    this.deferred = (ClassEncoding<Source>) resolver.detectClassEncoding(type).orElseGet(() -> {
      if (type.isAbstract()) {
        final List<SubType<Object>> subTypes = resolver.detectSubTypes(type);
        return new AbstractClassEncoding<>(type, subTypes, Optional.empty());
      }

      throw new RuntimeException("Unable to detect binding for type: " + type);
    });
  }
}
