package eu.toolchain.scribe.jackson;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import eu.toolchain.scribe.EntityResolver;
import eu.toolchain.scribe.Option;
import eu.toolchain.scribe.TypeDecoderProvider;
import eu.toolchain.scribe.TypeEncoderProvider;
import eu.toolchain.scribe.TypeReference;
import eu.toolchain.scribe.TypeStreamEncoderProvider;
import lombok.Data;

import java.lang.reflect.Type;

@Data
public class JacksonEntityMapper {
  private final EntityResolver resolver;
  private final JsonFactory factory;

  private final TypeEncoderProvider<JsonNode> encoderProvider;
  private final TypeStreamEncoderProvider<JsonGenerator> streamEncoderProvider;
  private final TypeDecoderProvider<JsonNode> decoderProvider;

  public JacksonEntityMapper(final EntityResolver resolver) {
    this(resolver, new JsonFactory());
  }

  public JacksonEntityMapper(final EntityResolver resolver, final JsonFactory factory) {
    this.resolver = resolver;
    this.factory = factory;

    final JacksonEncodingFactory f = new JacksonEncodingFactory();

    this.encoderProvider = resolver.encoderFor(f);
    this.streamEncoderProvider = resolver.streamEncoderFor(f);
    this.decoderProvider = resolver.decoderFor(f);
  }

  public JacksonEncoding<Object> encodingForType(final Type type) {
    return new JacksonEncoding<>(encoderProvider.newEncoderForType(type),
        streamEncoderProvider.newStreamEncoder(type), decoderProvider.newDecoderForType(type),
        factory);
  }

  public <T> JacksonEncoding<T> encodingFor(final Class<T> type) {
    return new JacksonEncoding<>(encoderProvider.newEncoder(type),
        streamEncoderProvider.newStreamEncoder(type), decoderProvider.newDecoder(type), factory);
  }

  public <T> JacksonEncoding<T> encodingFor(final TypeReference<T> type) {
    return new JacksonEncoding<>(encoderProvider.newEncoder(type),
        streamEncoderProvider.newStreamEncoder(type), decoderProvider.newDecoder(type), factory);
  }

  public JacksonEntityMapper withOptions(final Option... options) {
    return new JacksonEntityMapper(resolver.withOptions(options), factory);
  }
}
