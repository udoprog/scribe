package eu.toolchain.scribe.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import eu.toolchain.scribe.Decoded;
import eu.toolchain.scribe.Decoder;
import eu.toolchain.scribe.DecoderFactory;
import eu.toolchain.scribe.DecoderRegistry;
import eu.toolchain.scribe.Encoder;
import eu.toolchain.scribe.EncoderFactory;
import eu.toolchain.scribe.EncoderRegistry;
import eu.toolchain.scribe.EntityFieldsDecoder;
import eu.toolchain.scribe.EntityFieldsEncoder;
import eu.toolchain.scribe.EntityFieldsStreamEncoder;
import eu.toolchain.scribe.EntityResolver;
import eu.toolchain.scribe.JavaType;
import eu.toolchain.scribe.StreamEncoder;
import eu.toolchain.scribe.StreamEncoderFactory;
import eu.toolchain.scribe.StreamEncodingRegistry;
import eu.toolchain.scribe.jackson.encoding.AbstractVisitor;
import eu.toolchain.scribe.jackson.encoding.BooleanDecoder;
import eu.toolchain.scribe.jackson.encoding.BooleanEncoder;
import eu.toolchain.scribe.jackson.encoding.BooleanStreamEncoder;
import eu.toolchain.scribe.jackson.encoding.DoubleEncoder;
import eu.toolchain.scribe.jackson.encoding.DoubleStreamEncoder;
import eu.toolchain.scribe.jackson.encoding.FloatEncoder;
import eu.toolchain.scribe.jackson.encoding.FloatStreamEncoder;
import eu.toolchain.scribe.jackson.encoding.IntegerEncoder;
import eu.toolchain.scribe.jackson.encoding.IntegerStreamEncoder;
import eu.toolchain.scribe.jackson.encoding.ListDecoder;
import eu.toolchain.scribe.jackson.encoding.ListEncoder;
import eu.toolchain.scribe.jackson.encoding.ListStreamEncoder;
import eu.toolchain.scribe.jackson.encoding.LongEncoder;
import eu.toolchain.scribe.jackson.encoding.LongStreamEncoder;
import eu.toolchain.scribe.jackson.encoding.MapDecoder;
import eu.toolchain.scribe.jackson.encoding.MapEncoder;
import eu.toolchain.scribe.jackson.encoding.MapStreamEncoder;
import eu.toolchain.scribe.jackson.encoding.NumberDecoder;
import eu.toolchain.scribe.jackson.encoding.ShortEncoder;
import eu.toolchain.scribe.jackson.encoding.ShortStreamEncoder;
import eu.toolchain.scribe.jackson.encoding.StringDecoder;
import eu.toolchain.scribe.jackson.encoding.StringEncoder;
import eu.toolchain.scribe.jackson.encoding.StringStreamEncoder;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static eu.toolchain.scribe.TypeMatcher.any;
import static eu.toolchain.scribe.TypeMatcher.isPrimitive;
import static eu.toolchain.scribe.TypeMatcher.type;

@RequiredArgsConstructor
public class JacksonEncodingFactory
    implements EncoderFactory<JsonNode>, DecoderFactory<JsonNode>,
    StreamEncoderFactory<JsonGenerator> {
  private static StreamEncodingRegistry<JsonGenerator> streamRegistry =
      new StreamEncodingRegistry<>();
  private static EncoderRegistry<JsonNode> encoders = new EncoderRegistry<>();
  private static DecoderRegistry<JsonNode> decoders = new DecoderRegistry<>();

  private final EntityResolver resolver;

  static {
    streamRegistry.streamEncoder(type(Map.class, any(), any()), (resolver, type, factory) -> {
      final JavaType first = type.getTypeParameter(0).get();
      final JavaType second = type.getTypeParameter(1).get();

      if (!JavaType.of(String.class).equals(first)) {
        throw new IllegalArgumentException("First type argument must be String (" + type + ")");
      }

      final StreamEncoder<JsonGenerator, Object> value =
          resolver.mapping(second).newStreamEncoderImmediate(resolver, factory);
      return new MapStreamEncoder<>(value);
    });

    streamRegistry.streamEncoder(type(List.class, any()), (resolver, type, factory) -> {
      final JavaType first = type.getTypeParameter(0).get();

      final StreamEncoder<JsonGenerator, Object> value =
          resolver.mapping(first).newStreamEncoderImmediate(resolver, factory);

      return new ListStreamEncoder<>(value);
    });

    streamRegistry.streamEncoder(type(String.class),
        (resolver, type, factory) -> StringStreamEncoder.get());

    streamRegistry.streamEncoder(isPrimitive(Boolean.class),
        (resolver, type, factory) -> BooleanStreamEncoder.get());

    streamRegistry.streamEncoder(isPrimitive(Short.class),
        (resolver, type, factory) -> ShortStreamEncoder.get());
    streamRegistry.streamEncoder(isPrimitive(Integer.class),
        (resolver, type, factory) -> IntegerStreamEncoder.get());
    streamRegistry.streamEncoder(isPrimitive(Long.class),
        (resolver, type, factory) -> LongStreamEncoder.get());

    streamRegistry.streamEncoder(isPrimitive(Float.class),
        (resolver, type, factory) -> FloatStreamEncoder.get());

    streamRegistry.streamEncoder(isPrimitive(Double.class),
        (resolver, type, factory) -> DoubleStreamEncoder.get());
  }

  static {
    encoders.encoder(type(Map.class, any(), any()), (resolver, type, factory) -> {
      final JavaType first = type.getTypeParameter(0).get();
      final JavaType second = type.getTypeParameter(1).get();

      if (!JavaType.of(String.class).equals(first)) {
        throw new IllegalArgumentException("First type argument must be String (" + type + ")");
      }

      final Encoder<JsonNode, Object> value =
          resolver.mapping(second).newEncoderImmediate(resolver, factory);
      return new MapEncoder<>(value);
    });

    encoders.encoder(type(List.class, any()), (resolver, type, factory) -> {
      final JavaType first = type.getTypeParameter(0).get();

      final Encoder<JsonNode, Object> value =
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

      final Decoder<JsonNode, Object> value =
          resolver.mapping(second).newDecoderImmediate(resolver, factory);
      return new MapDecoder<>(value);
    });

    decoders.decoder(type(List.class, any()), (resolver, type, factory) -> {
      final JavaType first = type.getTypeParameter(0).get();

      final Decoder<JsonNode, Object> value =
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
  public <Source> Stream<Encoder<JsonNode, Source>> newEncoder(
      final EntityResolver resolver, final JavaType type
  ) {
    return encoders.newEncoder(resolver, type, this);
  }

  @Override
  public <Source> Stream<StreamEncoder<JsonGenerator, Source>> newStreamEncoder(
      final EntityResolver resolver, final JavaType type
  ) {
    return streamRegistry.newStreamEncoder(resolver, type, this);
  }

  @Override
  public <Source> Stream<Decoder<JsonNode, Source>> newDecoder(
      final EntityResolver resolver, final JavaType type
  ) {
    return decoders.newDecoder(resolver, type, this);
  }

  @Override
  public EntityFieldsEncoder<JsonNode> newEntityEncoder() {
    return new JacksonEntityFieldsEncoder();
  }

  @Override
  public EntityFieldsStreamEncoder<JsonGenerator> newEntityStreamEncoder() {
    return new JacksonEntityFieldsStreamEncoder();
  }

  private static final AbstractVisitor<Map<String, JsonNode>> OBJECT_VISITOR =
      new AbstractVisitor<Map<String, JsonNode>>() {
        @Override
        public Decoded<Map<String, JsonNode>> visitObject(
            final JsonNode.ObjectJsonNode object
        ) {
          return Decoded.of(object.getFields());
        }
      };

  @Override
  public Decoded<EntityFieldsDecoder<JsonNode>> newEntityDecoder(final JsonNode instance) {
    return instance.visit(OBJECT_VISITOR).map(JacksonEntityFieldsDecoder::new);
  }
}
