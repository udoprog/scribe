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
import eu.toolchain.scribe.Flags;
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
import eu.toolchain.scribe.reflection.JavaType;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static eu.toolchain.scribe.TypeMatcher.any;
import static eu.toolchain.scribe.TypeMatcher.isPrimitive;
import static eu.toolchain.scribe.TypeMatcher.type;

@RequiredArgsConstructor
public class JacksonEncodingFactory
    implements EncoderFactory<JsonNode, JsonNode.ObjectJsonNode>, DecoderFactory<JsonNode,
    JsonNode.ObjectJsonNode>, StreamEncoderFactory<JsonGenerator> {
  private static StreamEncodingRegistry<JsonGenerator> streamRegistry =
      new StreamEncodingRegistry<>();
  private static EncoderRegistry<JsonNode, JsonNode.ObjectJsonNode> encoders =
      new EncoderRegistry<>();
  private static DecoderRegistry<JsonNode, JsonNode.ObjectJsonNode> decoders =
      new DecoderRegistry<>();

  static {
    streamRegistry.setup(type(Map.class, any(), any()), (resolver, type, factory) -> {
      final JavaType first = type.getTypeParameter(0).get();
      final JavaType second = type.getTypeParameter(1).get();

      if (!JavaType.of(String.class).equals(first)) {
        throw new IllegalArgumentException("First type argument must be String (" + type + ")");
      }

      return resolver
          .mapping(second)
          .newStreamEncoder(resolver, factory)
          .map(MapStreamEncoder::new);
    });

    streamRegistry.setup(type(List.class, any()), (resolver, type, factory) -> {
      final JavaType first = type.getTypeParameter(0).get();

      return resolver
          .mapping(first)
          .newStreamEncoder(resolver, factory)
          .map(ListStreamEncoder::new);
    });

    streamRegistry.constant(type(String.class), StringStreamEncoder.get());
    streamRegistry.constant(isPrimitive(Boolean.class), BooleanStreamEncoder.get());
    streamRegistry.constant(isPrimitive(Short.class), ShortStreamEncoder.get());
    streamRegistry.constant(isPrimitive(Integer.class), IntegerStreamEncoder.get());
    streamRegistry.constant(isPrimitive(Long.class), LongStreamEncoder.get());
    streamRegistry.constant(isPrimitive(Float.class), FloatStreamEncoder.get());
    streamRegistry.constant(isPrimitive(Double.class), DoubleStreamEncoder.get());
  }

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
    decoders.simple(isPrimitive(Short.class), () -> NumberDecoder.SHORT);
    decoders.simple(isPrimitive(Integer.class), () -> NumberDecoder.INTEGER);
    decoders.simple(isPrimitive(Long.class), () -> NumberDecoder.LONG);
    decoders.simple(isPrimitive(Float.class), () -> NumberDecoder.FLOAT);
    decoders.simple(isPrimitive(Double.class), () -> NumberDecoder.DOUBLE);
  }

  @Override
  public <Source> Stream<Encoder<JsonNode, Source>> newEncoder(
      final EntityResolver resolver, final JavaType type, final Flags flags
  ) {
    return encoders.newEncoder(resolver, type, this);
  }

  @Override
  public <Source> Stream<StreamEncoder<JsonGenerator, Source>> newStreamEncoder(
      final EntityResolver resolver, final JavaType type, final Flags flags
  ) {
    return streamRegistry.newStreamEncoder(resolver, type, this);
  }

  @Override
  public <Source> Stream<Decoder<JsonNode, Source>> newDecoder(
      final EntityResolver resolver, final JavaType type, final Flags flags
  ) {
    return decoders.newDecoder(resolver, type, this);
  }

  @Override
  public EntityFieldsEncoder<JsonNode, JsonNode.ObjectJsonNode> newEntityEncoder() {
    return new JacksonEntityFieldsEncoder();
  }

  @Override
  public EntityFieldsStreamEncoder<JsonGenerator> newEntityStreamEncoder() {
    return new JacksonEntityFieldsStreamEncoder();
  }

  private static final AbstractVisitor<JsonNode.ObjectJsonNode> OBJECT_VISITOR =
      new AbstractVisitor<JsonNode.ObjectJsonNode>() {
        @Override
        public Decoded<JsonNode.ObjectJsonNode> visitObject(
            final JsonNode.ObjectJsonNode object
        ) {
          return Decoded.of(object);
        }
      };

  @Override
  public EntityFieldsDecoder<JsonNode> newEntityDecoder(
      final JsonNode.ObjectJsonNode instance
  ) {
    return new JacksonEntityFieldsDecoder(instance.getFields());
  }

  @Override
  public JsonNode entityAsValue(final JsonNode.ObjectJsonNode entity) {
    return entity;
  }

  @Override
  public Decoded<JsonNode.ObjectJsonNode> valueAsEntity(final JsonNode instance) {
    return instance.visit(OBJECT_VISITOR);
  }
}
