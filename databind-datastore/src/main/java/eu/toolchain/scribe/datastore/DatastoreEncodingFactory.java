package eu.toolchain.scribe.datastore;

import com.google.datastore.v1.Value;

import eu.toolchain.scribe.Decoded;
import eu.toolchain.scribe.Decoder;
import eu.toolchain.scribe.DecoderFactory;
import eu.toolchain.scribe.DecoderRegistry;
import eu.toolchain.scribe.Encoder;
import eu.toolchain.scribe.EncoderFactory;
import eu.toolchain.scribe.EncoderRegistry;
import eu.toolchain.scribe.EntityFieldsDecoder;
import eu.toolchain.scribe.EntityFieldsEncoder;
import eu.toolchain.scribe.EntityResolver;
import eu.toolchain.scribe.JavaType;
import eu.toolchain.scribe.datastore.encoding.BooleanDecoder;
import eu.toolchain.scribe.datastore.encoding.BooleanEncoder;
import eu.toolchain.scribe.datastore.encoding.DoubleEncoder;
import eu.toolchain.scribe.datastore.encoding.FloatEncoder;
import eu.toolchain.scribe.datastore.encoding.IntegerEncoder;
import eu.toolchain.scribe.datastore.encoding.ListDecoder;
import eu.toolchain.scribe.datastore.encoding.ListEncoder;
import eu.toolchain.scribe.datastore.encoding.LongEncoder;
import eu.toolchain.scribe.datastore.encoding.MapDecoder;
import eu.toolchain.scribe.datastore.encoding.MapEncoder;
import eu.toolchain.scribe.datastore.encoding.NumberDecoder;
import eu.toolchain.scribe.datastore.encoding.ShortEncoder;
import eu.toolchain.scribe.datastore.encoding.StringDecoder;
import eu.toolchain.scribe.datastore.encoding.StringEncoder;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import lombok.RequiredArgsConstructor;

import static eu.toolchain.scribe.TypeMatcher.any;
import static eu.toolchain.scribe.TypeMatcher.isPrimitive;
import static eu.toolchain.scribe.TypeMatcher.type;

@RequiredArgsConstructor
public class DatastoreEncodingFactory
    implements EncoderFactory<Value>, DecoderFactory<Value> {
  private static EncoderRegistry<Value> encoders = new EncoderRegistry<>();
  private static DecoderRegistry<Value> decoders = new DecoderRegistry<>();

  private final EntityResolver resolver;

  static {
    encoders.encoder(type(Map.class, any(), any()), (resolver, type, factory) -> {
      final JavaType first = type.getTypeParameter(0).get();
      final JavaType second = type.getTypeParameter(1).get();

      if (!JavaType.of(String.class).equals(first)) {
        throw new IllegalArgumentException("First type argument must be String (" + type + ")");
      }

      final Encoder<Value, Object> value =
          resolver.mapping(second).newEncoderImmediate(resolver, factory);
      return new MapEncoder<>(value);
    });

    encoders.encoder(type(List.class, any()), (resolver, type, factory) -> {
      final JavaType first = type.getTypeParameter(0).get();

      final Encoder<Value, Object> value =
          resolver.mapping(first).newEncoderImmediate(resolver, factory);

      return new ListEncoder<>(value);
    });

    encoders.encoder(type(String.class), (resolver, type, factory) -> StringEncoder.get());

    encoders.encoder(isPrimitive(Boolean.class), (resolver, type, factory) -> BooleanEncoder.get());

    encoders.encoder(isPrimitive(Short.class), (resolver, type, factory) -> ShortEncoder.get());
    encoders.encoder(isPrimitive(Integer.class), (resolver, type, factory) -> IntegerEncoder.get());
    encoders.encoder(isPrimitive(Long.class), (resolver, type, factory) -> LongEncoder.get());

    encoders.encoder(isPrimitive(Float.class), (resolver, type, factory) -> FloatEncoder.get());

    encoders.encoder(isPrimitive(Double.class), (resolver, type, factory) -> DoubleEncoder.get());
  }

  static {
    decoders.decoder(type(Map.class, any(), any()), (resolver, type, factory) -> {
      final JavaType first = type.getTypeParameter(0).get();
      final JavaType second = type.getTypeParameter(1).get();

      if (!JavaType.of(String.class).equals(first)) {
        throw new IllegalArgumentException("First type argument must be String (" + type + ")");
      }

      final Decoder<Value, Object> value =
          resolver.mapping(second).newDecoderImmediate(resolver, factory);
      return new MapDecoder<>(value);
    });

    decoders.decoder(type(List.class, any()), (resolver, type, factory) -> {
      final JavaType first = type.getTypeParameter(0).get();

      final Decoder<Value, Object> value =
          resolver.mapping(first).newDecoderImmediate(resolver, factory);

      return new ListDecoder<>(value);
    });

    decoders.decoder(type(String.class), (resolver, type, factory) -> StringDecoder.get());

    decoders.decoder(isPrimitive(Boolean.class), (resolver, type, factory) -> BooleanDecoder.get());

    decoders.decoder(isPrimitive(Short.class), (resolver, type, factory) -> NumberDecoder.SHORT);
    decoders.decoder(isPrimitive(Integer.class),
        (resolver, type, factory) -> NumberDecoder.INTEGER);
    decoders.decoder(isPrimitive(Long.class), (resolver, type, factory) -> NumberDecoder.LONG);
    decoders.decoder(isPrimitive(Float.class), (resolver, type, factory) -> NumberDecoder.FLOAT);
    decoders.decoder(isPrimitive(Double.class), (resolver, type, factory) -> NumberDecoder.DOUBLE);
  }

  @Override
  public <Source> Stream<Encoder<Value, Source>> newEncoder(
      final EntityResolver resolver, final JavaType type
  ) {
    return encoders.newEncoder(resolver, type, this);
  }

  @Override
  public <Source> Stream<Decoder<Value, Source>> newDecoder(
      final EntityResolver resolver, final JavaType type
  ) {
    return decoders.newDecoder(resolver, type, this);
  }

  @Override
  public EntityFieldsEncoder<Value> newEntityEncoder() {
    return new DatastoreEntityFieldsEncoder();
  }

  @Override
  public Decoded<EntityFieldsDecoder<Value>> newEntityDecoder(final Value instance) {
    switch (instance.getValueTypeCase()) {
      case ENTITY_VALUE:
        return Decoded
            .of(new DatastoreEntityFieldsDecoder(instance.getEntityValue().getProperties()));
      default:
        return Decoded.absent();
    }
  }
}
