package eu.toolchain.scribe;

import eu.toolchain.scribe.reflection.JavaType;
import lombok.Data;

import java.util.stream.Stream;

@Data
public class ValueMapping<Source> implements Mapping<Source> {
  private final EncodeValue<Source> encodeValue;
  private final DecodeValue<Source> decodeValue;

  @Override
  public JavaType getType() {
    return encodeValue.getSourceType();
  }

  @Override
  public <Target, EntityTarget> Stream<Encoder<Target, Source>> newEncoder(
      final EntityResolver resolver, final EncoderFactory<Target, EntityTarget> factory,
      final Flags flags
  ) {
    return encodeValue.newEncoder(resolver, factory, flags);
  }

  @Override
  public <Target> Stream<StreamEncoder<Target, Source>> newStreamEncoder(
      final EntityResolver resolver, final StreamEncoderFactory<Target> factory, final Flags flags
  ) {
    return encodeValue.newStreamEncoder(resolver, factory, flags);
  }

  @Override
  public <Target, EntityTarget> Stream<Decoder<Target, Source>> newDecoder(
      final EntityResolver resolver, final DecoderFactory<Target, EntityTarget> factory,
      final Flags flags
  ) {
    return decodeValue.newDecoder(resolver, factory, flags);
  }

  @Override
  public void postCacheInitialize(final EntityResolver resolver) {
    encodeValue.initialize(resolver);
    decodeValue.initialize(resolver);
  }
}
