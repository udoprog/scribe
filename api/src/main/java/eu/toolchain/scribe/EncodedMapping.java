package eu.toolchain.scribe;

import eu.toolchain.scribe.reflection.JavaType;
import lombok.Data;

import java.util.stream.Stream;

/**
 * Mapping that indicated that the current type has a custom target encoding.
 */
@Data
public class EncodedMapping implements Mapping {
  private final JavaType type;

  @Override
  public <Target, Source> Stream<Encoder<Target, Source>> newEncoder(
      final EntityResolver resolver, final EncoderFactory<Target> factory, final Flags flags
  ) {
    return factory.newEncoder(resolver, type, flags);
  }

  @Override
  public <Target, Source> Stream<StreamEncoder<Target, Source>> newStreamEncoder(
      final EntityResolver resolver, final StreamEncoderFactory<Target> factory, final Flags flags
  ) {
    return factory.newStreamEncoder(resolver, type, flags);
  }

  @Override
  public <Target, Source> Stream<Decoder<Target, Source>> newDecoder(
      final EntityResolver resolver, final DecoderFactory<Target> factory, final Flags flags
  ) {
    return factory.newDecoder(resolver, type, flags);
  }
}
