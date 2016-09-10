package eu.toolchain.scribe.typesafe;

import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigObject;
import com.typesafe.config.ConfigValue;
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
import eu.toolchain.scribe.StringEncoding;
import eu.toolchain.scribe.StringMapper;
import eu.toolchain.scribe.TypeDecoderProvider;
import eu.toolchain.scribe.TypeEncoderProvider;
import lombok.Data;

import java.lang.reflect.Type;

@Data
public class TypeSafeMapper
    implements ConverterMapper<ConfigValue>, EntityConverterMapper<ConfigObject>, StringMapper {
  private final EntityResolver resolver;

  private final TypeEncoderProvider<ConfigValue> encoderProvider;
  private final TypeDecoderProvider<ConfigValue> decoderProvider;

  public TypeSafeMapper(final EntityResolver resolver) {
    this.resolver = resolver;

    this.encoderProvider = resolver.encoderFor(new TypeSafeEncoderFactory());
    this.decoderProvider = resolver.decoderFor(new TypeSafeDecoderFactory());
  }

  @Override
  public StringEncoding<Object> stringEncodingForType(final Type type) {
    return valueEncodingForType(type).toStringEncoding(ConfigValue::render,
        string -> ConfigFactory.parseString(string).root());
  }

  @Override
  public ConverterEncoding<Object, ConfigValue> valueEncodingForType(final Type type) {
    final Encoder<ConfigValue, Object> encoder = encoderProvider.newEncoderForType(type);
    final Decoder<ConfigValue, Object> decoder = decoderProvider.newDecoderForType(type);

    return new ConverterEncoding<Object, ConfigValue>() {
      @Override
      public ConfigValue encode(final Object instance) {
        return encoder.encode(instance);
      }

      @Override
      public Object decode(final ConfigValue value) {
        return decoder
            .decode(Context.ROOT, value)
            .orElseThrow(() -> new IllegalArgumentException("Value decoded to nothing"));
      }
    };
  }

  @Override
  public ConverterEncoding<Object, ConfigObject> entityEncodingForType(final Type type) {
    final Encoder<ConfigValue, Object> valueEncoder = this.encoderProvider.newEncoderForType(type);
    final Decoder<ConfigValue, Object> valueDecoder = this.decoderProvider.newDecoderForType(type);

    if (!(valueEncoder instanceof EntityEncoder)) {
      throw new IllegalStateException("Encoder is not for entities");
    }

    if (!(valueDecoder instanceof EntityDecoder)) {
      throw new IllegalStateException("Decoder is not for entities");
    }

    final EntityEncoder<ConfigValue, ConfigObject, Object> encoder =
        (EntityEncoder<ConfigValue, ConfigObject, Object>) valueEncoder;
    final EntityDecoder<ConfigValue, ConfigObject, Object> decoder =
        (EntityDecoder<ConfigValue, ConfigObject, Object>) valueDecoder;

    return new ConverterEncoding<Object, ConfigObject>() {
      public ConfigObject encode(Object instance) {
        return encoder.encodeEntity(Context.ROOT, instance);
      }

      public Object decode(ConfigObject instance) {
        return decoder.decodeEntity(Context.ROOT, instance);
      }
    };
  }

  public TypeSafeMapper withOptions(final Option... options) {
    return new TypeSafeMapper(resolver.withOptions(options));
  }
}
