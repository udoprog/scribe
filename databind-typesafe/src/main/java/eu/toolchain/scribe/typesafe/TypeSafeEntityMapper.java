package eu.toolchain.scribe.typesafe;

import com.typesafe.config.ConfigValue;
import eu.toolchain.scribe.EntityResolver;
import eu.toolchain.scribe.Option;
import eu.toolchain.scribe.TypeDecoderProvider;
import eu.toolchain.scribe.TypeEncoderProvider;
import eu.toolchain.scribe.TypeReference;
import lombok.Data;

import java.lang.reflect.Type;

@Data
public class TypeSafeEntityMapper {
  private final EntityResolver resolver;

  private final TypeEncoderProvider<ConfigValue> encoderProvider;
  private final TypeDecoderProvider<ConfigValue> decoderProvider;

  public TypeSafeEntityMapper(final EntityResolver resolver) {
    this.resolver = resolver;

    this.encoderProvider = resolver.encoderFor(new TypeSafeEncoderFactory());
    this.decoderProvider = resolver.decoderFor(new TypeSafeDecoderFactory());
  }

  public TypeSafeEncoding<Object> encodingForType(final Type type) {
    return new TypeSafeEncoding<>(encoderProvider.newEncoder(type),
        decoderProvider.newDecoder(type));
  }

  public <T> TypeSafeEncoding<T> encodingFor(final Class<T> type) {
    return new TypeSafeEncoding<>(encoderProvider.newEncoder(type),
        decoderProvider.newDecoder(type));
  }

  public <T> TypeSafeEncoding<T> encodingFor(final TypeReference<T> type) {
    return new TypeSafeEncoding<>(encoderProvider.newEncoder(type),
        decoderProvider.newDecoder(type));
  }

  public TypeSafeEntityMapper withOptions(final Option... options) {
    return new TypeSafeEntityMapper(resolver.withOptions(options));
  }
}
