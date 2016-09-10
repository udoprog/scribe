package eu.toolchain.scribe.datastore;

import com.google.datastore.v1.Entity;
import com.google.datastore.v1.Key;
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
import eu.toolchain.scribe.Flags;
import eu.toolchain.scribe.datastore.encoding.BooleanDecoder;
import eu.toolchain.scribe.datastore.encoding.BooleanEncoder;
import eu.toolchain.scribe.datastore.encoding.DoubleEncoder;
import eu.toolchain.scribe.datastore.encoding.ExcludeFromIndexesDecoder;
import eu.toolchain.scribe.datastore.encoding.ExcludeFromIndexesEncoder;
import eu.toolchain.scribe.datastore.encoding.FloatEncoder;
import eu.toolchain.scribe.datastore.encoding.IntegerEncoder;
import eu.toolchain.scribe.datastore.encoding.KeyDecoder;
import eu.toolchain.scribe.datastore.encoding.KeyEncoder;
import eu.toolchain.scribe.datastore.encoding.ListDecoder;
import eu.toolchain.scribe.datastore.encoding.ListEncoder;
import eu.toolchain.scribe.datastore.encoding.LongEncoder;
import eu.toolchain.scribe.datastore.encoding.MapDecoder;
import eu.toolchain.scribe.datastore.encoding.MapEncoder;
import eu.toolchain.scribe.datastore.encoding.NumberDecoder;
import eu.toolchain.scribe.datastore.encoding.ShortEncoder;
import eu.toolchain.scribe.datastore.encoding.StringDecoder;
import eu.toolchain.scribe.datastore.encoding.StringEncoder;
import eu.toolchain.scribe.datastore.encoding.ValueDecoder;
import eu.toolchain.scribe.datastore.encoding.ValueEncoder;
import eu.toolchain.scribe.reflection.JavaType;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static eu.toolchain.scribe.TypeMatcher.any;
import static eu.toolchain.scribe.TypeMatcher.instance;
import static eu.toolchain.scribe.TypeMatcher.isPrimitive;
import static eu.toolchain.scribe.TypeMatcher.type;

@RequiredArgsConstructor
public class DatastoreEncodingFactory implements EncoderFactory<Value>, DecoderFactory<Value> {
  private static EncoderRegistry<Value> encoders = new EncoderRegistry<>();
  private static DecoderRegistry<Value> decoders = new DecoderRegistry<>();

  private final EntityResolver resolver;

  static {
    encoders.setup(type(Map.class, any(), any()), (resolver, type, factory) -> {
      final JavaType first = type.getTypeParameter(0).get();
      final JavaType second = type.getTypeParameter(1).get();

      if (!JavaType.of(String.class).equals(first)) {
        throw new IllegalArgumentException("First type argument must be String (" + type + ")");
      }

      return resolver.mapping(second).newEncoder(resolver, factory).map(MapEncoder::new);
    });

    encoders.setup(type(List.class, any()), (resolver, type, factory) -> {
      final JavaType first = type.getTypeParameter(0).get();

      return resolver.mapping(first).newEncoder(resolver, factory).map(ListEncoder::new);
    });

    encoders.constant(type(String.class), StringEncoder.get());
    encoders.constant(isPrimitive(Boolean.class), BooleanEncoder.get());
    encoders.constant(isPrimitive(Short.class), ShortEncoder.get());
    encoders.constant(isPrimitive(Integer.class), IntegerEncoder.get());
    encoders.constant(isPrimitive(Long.class), LongEncoder.get());
    encoders.constant(isPrimitive(Float.class), FloatEncoder.get());
    encoders.constant(isPrimitive(Double.class), DoubleEncoder.get());
    encoders.constant(instance(Value.class), ValueEncoder.get());
    encoders.constant(instance(Key.class), KeyEncoder.get());
  }

  static {
    decoders.setup(type(Map.class, any(), any()), (resolver, type, factory) -> {
      final JavaType first = type.getTypeParameter(0).get();
      final JavaType second = type.getTypeParameter(1).get();

      if (!JavaType.of(String.class).equals(first)) {
        throw new IllegalArgumentException("First type argument must be String (" + type + ")");
      }

      return resolver.mapping(second).newDecoder(resolver, factory).map(MapDecoder::new);
    });

    decoders.setup(type(List.class, any()), (resolver, type, factory) -> {
      final JavaType first = type.getTypeParameter(0).get();

      return resolver.mapping(first).newDecoder(resolver, factory).map(ListDecoder::new);
    });

    decoders.constant(type(String.class), StringDecoder.get());
    decoders.constant(isPrimitive(Boolean.class), BooleanDecoder.get());
    decoders.constant(isPrimitive(Short.class), NumberDecoder.SHORT);
    decoders.constant(isPrimitive(Integer.class), NumberDecoder.INTEGER);
    decoders.constant(isPrimitive(Long.class), NumberDecoder.LONG);
    decoders.constant(isPrimitive(Float.class), NumberDecoder.FLOAT);
    decoders.constant(isPrimitive(Double.class), NumberDecoder.DOUBLE);
    decoders.constant(instance(Value.class), ValueDecoder.get());
    decoders.constant(instance(Key.class), KeyDecoder.get());
  }

  @Override
  public <Source> Stream<Encoder<Value, Source>> newEncoder(
      final EntityResolver resolver, final JavaType type, final Flags flags
  ) {
    Stream<Encoder<Value, Source>> encoder = encoders.newEncoder(resolver, type, this);

    if (flags.getFlag(DatastoreFlags.ExcludeFromIndexes.class).findFirst().isPresent()) {
      encoder = encoder.map(ExcludeFromIndexesEncoder::new);
    }

    return encoder;
  }

  @Override
  public <Source> Stream<Decoder<Value, Source>> newDecoder(
      final EntityResolver resolver, final JavaType type, final Flags flags
  ) {
    Stream<Decoder<Value, Source>> decoder = decoders.newDecoder(resolver, type, this);

    final boolean strictCheck = flags
        .getFlag(DatastoreFlags.ExcludeFromIndexes.class)
        .map(DatastoreFlags.ExcludeFromIndexes::isDecode)
        .findFirst()
        .orElse(false);

    if (strictCheck) {
      decoder = decoder.map(ExcludeFromIndexesDecoder::new);
    }

    return decoder;
  }

  @Override
  public EntityFieldsEncoder<Value> newEntityEncoder() {
    return new DatastoreEntityFieldsEncoder();
  }

  @Override
  public Decoded<EntityFieldsDecoder<Value>> newEntityDecoder(final Value instance) {
    switch (instance.getValueTypeCase()) {
      case ENTITY_VALUE:
        final Entity entity = instance.getEntityValue();
        return Decoded.of(new DatastoreEntityFieldsDecoder(() -> entity.hasKey() ? Decoded.of(
            Value.newBuilder().setKeyValue(entity.getKey()).build()) : Decoded.absent(),
            entity.getProperties()));
      default:
        return Decoded.absent();
    }
  }
}
