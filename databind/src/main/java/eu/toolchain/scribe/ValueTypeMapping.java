package eu.toolchain.scribe;

import eu.toolchain.scribe.typemapping.DecodeValue;
import eu.toolchain.scribe.typemapping.EncodeValue;
import eu.toolchain.scribe.typemapping.TypeMapping;
import lombok.Data;

import java.util.Optional;

@Data
public class ValueTypeMapping implements TypeMapping {
  private final EncodeValue encodeValue;
  private final DecodeValue decodeValue;

  @Override
  public JavaType getType() {
    return encodeValue.getEntityType();
  }

  @Override
  public <Target, Source> Optional<Encoder<Target, Source>> newEncoder(
      final EntityResolver resolver, final EncoderFactory<Target> factory
  ) {
    return encodeValue.newEncoder(resolver, factory);
  }

  @Override
  public <Target, Source> Optional<StreamEncoder<Target, Source>> newStreamEncoder(
      final EntityResolver resolver, final StreamEncoderFactory<Target> factory
  ) {
    return encodeValue.newStreamEncoder(resolver, factory);
  }

  @Override
  public <Target, Source> Optional<Decoder<Target, Source>> newDecoder(
      final EntityResolver resolver, final DecoderFactory<Target> factory
  ) {
    return decodeValue.newDecoder(resolver, factory);
  }
}
