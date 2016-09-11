package eu.toolchain.scribe;

import eu.toolchain.scribe.reflection.JavaType;
import lombok.Data;

import java.util.stream.Stream;

/**
 * Mapping that indicated that the current type has a custom target encoding.
 */
@Data
public class EncodedMapping<Source> implements Mapping<Source> {
  private final JavaType type;

  @Override
  public <Target, EntityTarget> Stream<Encoder<Target, Source>> newEncoder(
      final EntityResolver resolver, final EncoderFactory<Target, EntityTarget> factory,
      final Flags flags
  ) {
    return factory.newEncoder(resolver, type, flags);
  }

  @Override
  public <Target> Stream<StreamEncoder<Target, Source>> newStreamEncoder(
      final EntityResolver resolver, final StreamEncoderFactory<Target> factory, final Flags flags
  ) {
    return factory.newStreamEncoder(resolver, type, flags);
  }

  @Override
  public <Target, EntityTarget> Stream<Decoder<Target, Source>> newDecoder(
      final EntityResolver resolver, final DecoderFactory<Target, EntityTarget> factory,
      final Flags flags
  ) {
    return factory.newDecoder(resolver, type, flags);
  }
}
