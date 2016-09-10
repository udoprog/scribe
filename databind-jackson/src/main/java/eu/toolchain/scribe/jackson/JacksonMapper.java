package eu.toolchain.scribe.jackson;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import eu.toolchain.scribe.Context;
import eu.toolchain.scribe.ConverterEncoding;
import eu.toolchain.scribe.ConverterMapper;
import eu.toolchain.scribe.Decoder;
import eu.toolchain.scribe.Encoder;
import eu.toolchain.scribe.EntityConverterMapper;
import eu.toolchain.scribe.EntityDecoder;
import eu.toolchain.scribe.EntityEncoder;
import eu.toolchain.scribe.EntityResolver;
import eu.toolchain.scribe.Option;
import eu.toolchain.scribe.StreamEncoder;
import eu.toolchain.scribe.StringEncoding;
import eu.toolchain.scribe.StringMapper;
import eu.toolchain.scribe.TypeDecoderProvider;
import eu.toolchain.scribe.TypeEncoderProvider;
import eu.toolchain.scribe.TypeStreamEncoderProvider;
import lombok.Data;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;

@Data
public class JacksonMapper
    implements ConverterMapper<JsonNode>, EntityConverterMapper<JsonNode.ObjectJsonNode>,
    StringMapper {
  private final EntityResolver resolver;
  private final JsonFactory factory;

  private final TypeEncoderProvider<JsonNode> encoderProvider;
  private final TypeStreamEncoderProvider<JsonGenerator> streamEncoderProvider;
  private final TypeDecoderProvider<JsonNode> decoderProvider;

  public JacksonMapper(final EntityResolver resolver) {
    this(resolver, new JsonFactory());
  }

  public JacksonMapper(final EntityResolver resolver, final JsonFactory factory) {
    this.resolver = resolver;
    this.factory = factory;

    final JacksonEncodingFactory f = new JacksonEncodingFactory();

    this.encoderProvider = resolver.encoderFor(f);
    this.streamEncoderProvider = resolver.streamEncoderFor(f);
    this.decoderProvider = resolver.decoderFor(f);
  }

  @Override
  public ConverterEncoding<Object, JsonNode> valueEncodingForType(final Type type) {
    final Encoder<JsonNode, Object> encoder = encoderProvider.newEncoderForType(type);
    final Decoder<JsonNode, Object> decoder = decoderProvider.newDecoderForType(type);

    return new ConverterEncoding<Object, JsonNode>() {
      @Override
      public JsonNode encode(final Object instance) {
        return encoder.encode(instance);
      }

      @Override
      public Object decode(final JsonNode value) {
        return decoder
            .decode(Context.ROOT, value)
            .orElseThrow(() -> new IllegalArgumentException("Value decoded to nothing"));
      }
    };
  }

  @Override
  public ConverterEncoding<Object, JsonNode.ObjectJsonNode> entityEncodingForType(
      final Type type
  ) {
    final Encoder<JsonNode, Object> valueEncoder = this.encoderProvider.newEncoderForType(type);
    final Decoder<JsonNode, Object> valueDecoder = this.decoderProvider.newDecoderForType(type);

    if (!(valueEncoder instanceof EntityEncoder)) {
      throw new IllegalStateException("Encoder is not for entities");
    }

    if (!(valueDecoder instanceof EntityDecoder)) {
      throw new IllegalStateException("Decoder is not for entities");
    }

    final EntityEncoder<JsonNode, JsonNode.ObjectJsonNode, Object> encoder =
        (EntityEncoder<JsonNode, JsonNode.ObjectJsonNode, Object>) valueEncoder;
    final EntityDecoder<JsonNode, JsonNode.ObjectJsonNode, Object> decoder =
        (EntityDecoder<JsonNode, JsonNode.ObjectJsonNode, Object>) valueDecoder;

    return new ConverterEncoding<Object, JsonNode.ObjectJsonNode>() {
      public JsonNode.ObjectJsonNode encode(Object instance) {
        return encoder.encodeEntity(Context.ROOT, instance);
      }

      public Object decode(JsonNode.ObjectJsonNode instance) {
        return decoder.decodeEntity(Context.ROOT, instance);
      }
    };
  }

  @Override
  public StringEncoding<Object> stringEncodingForType(final Type type) {
    final Decoder<JsonNode, Object> decoder = decoderProvider.newDecoderForType(type);
    final StreamEncoder<JsonGenerator, Object> streamEncoder =
        streamEncoderProvider.newStreamEncoder(type);

    return new StringEncoding<Object>() {
      @Override
      public Object decode(String json) {
        final JsonNode node;

        try (final JsonParser parser = factory.createParser(json)) {
          node = JsonNode.fromParser(parser);
        } catch (final IOException e) {
          throw new RuntimeException("failed to generate node", e);
        }

        return decoder
            .decode(Context.ROOT, node)
            .orElseThrow(() -> new IllegalArgumentException("String decoded to nothing"));
      }

      @Override
      public String encode(Object instance) {
        final Context path = Context.ROOT;

        try (final ByteArrayOutputStream out = new ByteArrayOutputStream()) {
          try (final JsonGenerator generator = factory.createGenerator(out)) {
            streamEncoder.streamEncode(path, instance, generator);
          }

          return new String(out.toByteArray(), StandardCharsets.UTF_8);
        } catch (final Exception e) {
          throw path.error("failed to generate json", e);
        }
      }
    };
  }

  public JacksonMapper withOptions(final Option... options) {
    return new JacksonMapper(resolver.withOptions(options), factory);
  }
}
