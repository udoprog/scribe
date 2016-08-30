package eu.toolchain.scribe;

import eu.toolchain.scribe.reflection.JavaType;
import lombok.Data;

import java.util.Optional;

@Data
public class ValueMapping implements Mapping {
  private final EncodeValue encodeValue;
  private final DecodeValue decodeValue;

  @Override
  public JavaType getType() {
    return encodeValue.getSourceType();
  }

  @Override
  public <Target, Source> Optional<Encoder<Target, Source>> newEncoder(
      final EntityResolver resolver, final Flags flags, final EncoderFactory<Target> factory
  ) {
    return encodeValue.newEncoder(resolver, factory);
  }

  @Override
  public <Target, Source> Optional<StreamEncoder<Target, Source>> newStreamEncoder(
      final EntityResolver resolver, final Flags flags, final StreamEncoderFactory<Target> factory
  ) {
    return encodeValue.newStreamEncoder(resolver, factory);
  }

  @Override
  public <Target, Source> Optional<Decoder<Target, Source>> newDecoder(
      final EntityResolver resolver, final Flags flags, final DecoderFactory<Target> factory
  ) {
    return decodeValue.newDecoder(resolver, factory);
  }
}
