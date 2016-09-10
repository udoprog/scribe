package eu.toolchain.scribe;

import eu.toolchain.scribe.reflection.JavaType;
import lombok.Data;

import java.util.stream.Stream;

@Data
public class ValueMapping implements Mapping {
  private final EncodeValue encodeValue;
  private final DecodeValue decodeValue;

  @Override
  public JavaType getType() {
    return encodeValue.getSourceType();
  }

  @Override
  public <Target, EntityTarget, Source> Stream<Encoder<Target, Source>> newEncoder(
      final EntityResolver resolver, final EncoderFactory<Target, EntityTarget> factory,
      final Flags flags
  ) {
    return encodeValue.newEncoder(resolver, factory, flags);
  }

  @Override
  public <Target, Source> Stream<StreamEncoder<Target, Source>> newStreamEncoder(
      final EntityResolver resolver, final StreamEncoderFactory<Target> factory, final Flags flags
  ) {
    return encodeValue.newStreamEncoder(resolver, factory, flags);
  }

  @Override
  public <Target, EntityTarget, Source> Stream<Decoder<Target, Source>> newDecoder(
      final EntityResolver resolver, final DecoderFactory<Target, EntityTarget> factory,
      final Flags flags
  ) {
    return decodeValue.newDecoder(resolver, factory, flags);
  }

  @Override
  public void initialize(final EntityResolver resolver) {
    encodeValue.initialize(resolver);
    decodeValue.initialize(resolver);
  }
}
