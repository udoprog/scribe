package eu.toolchain.scribe.datastore;

import com.google.datastore.v1.Entity;
import com.google.datastore.v1.Value;
import eu.toolchain.scribe.Context;
import eu.toolchain.scribe.ConverterEncoding;
import eu.toolchain.scribe.ConverterMapper;
import eu.toolchain.scribe.Decoded;
import eu.toolchain.scribe.Decoder;
import eu.toolchain.scribe.Encoder;
import eu.toolchain.scribe.EntityConverterMapper;
import eu.toolchain.scribe.EntityDecoder;
import eu.toolchain.scribe.EntityEncoder;
import eu.toolchain.scribe.EntityResolver;
import eu.toolchain.scribe.Option;
import eu.toolchain.scribe.TypeDecoderProvider;
import eu.toolchain.scribe.TypeEncoderProvider;
import lombok.Data;

import java.lang.reflect.Type;

@Data
public class DatastoreV1ProtosMapper
    implements EntityConverterMapper<Entity>, ConverterMapper<Value> {
  private final EntityResolver resolver;

  private final TypeEncoderProvider<Value> valueEncoder;
  private final TypeDecoderProvider<Value> valueDecoder;

  public DatastoreV1ProtosMapper(final EntityResolver resolver) {
    this.resolver = resolver;

    final DatastoreEncodingFactory f = new DatastoreEncodingFactory(resolver);

    this.valueEncoder = resolver.encoderFor(f);
    this.valueDecoder = resolver.decoderFor(f);
  }

  @Override
  public ConverterEncoding<Object, Value> valueEncodingForType(final Type type) {
    final Encoder<Value, Object> encoder = valueEncoder.newEncoderForType(type);
    final Decoder<Value, Object> decoder = valueDecoder.newDecoderForType(type);

    return new ConverterEncoding<Object, Value>() {
      public Value encode(Object instance) {
        return encoder.encode(Context.ROOT, instance);
      }

      public Object decode(Value instance) {
        final Decoded<Object> decoded = decoder.decode(Context.ROOT, instance);

        if (decoded == null) {
          throw Context.ROOT.error("decoder returned null");
        }

        return decoded.orElseThrow(() -> Context.ROOT.error("input decoded to nothing"));
      }
    };
  }

  @Override
  public ConverterEncoding<Object, Entity> entityEncodingForType(final Type type) {
    final Encoder<Value, Object> valueEncoder = this.valueEncoder.newEncoderForType(type);
    final Decoder<Value, Object> valueDecoder = this.valueDecoder.newDecoderForType(type);

    if (!(valueEncoder instanceof EntityEncoder)) {
      throw new IllegalStateException("Encoder is not for entities");
    }

    if (!(valueDecoder instanceof EntityDecoder)) {
      throw new IllegalStateException("Decoder is not for entities");
    }

    final EntityEncoder<Value, Entity, Object> encoder =
        (EntityEncoder<Value, Entity, Object>) valueEncoder;
    final EntityDecoder<Value, Entity, Object> decoder =
        (EntityDecoder<Value, Entity, Object>) valueDecoder;

    return new ConverterEncoding<Object, Entity>() {
      public Entity encode(Object instance) {
        return encoder.encodeEntity(Context.ROOT, instance);
      }

      public Object decode(Entity instance) {
        return decoder.decodeEntity(Context.ROOT, instance);
      }
    };
  }

  public DatastoreV1ProtosMapper withOptions(final Option... options) {
    return new DatastoreV1ProtosMapper(resolver.withOptions(options));
  }
}
