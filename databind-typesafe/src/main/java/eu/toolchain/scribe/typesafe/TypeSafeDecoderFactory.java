package eu.toolchain.scribe.typesafe;

import com.typesafe.config.ConfigObject;
import com.typesafe.config.ConfigValue;
import eu.toolchain.scribe.Decoded;
import eu.toolchain.scribe.Decoder;
import eu.toolchain.scribe.DecoderFactory;
import eu.toolchain.scribe.DecoderRegistry;
import eu.toolchain.scribe.EntityFieldsDecoder;
import eu.toolchain.scribe.EntityResolver;
import eu.toolchain.scribe.Flags;
import eu.toolchain.scribe.JavaType;
import eu.toolchain.scribe.typesafe.encoding.BooleanDecoder;
import eu.toolchain.scribe.typesafe.encoding.ListDecoder;
import eu.toolchain.scribe.typesafe.encoding.MapDecoder;
import eu.toolchain.scribe.typesafe.encoding.NumberDecoder;
import eu.toolchain.scribe.typesafe.encoding.StringDecoder;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static eu.toolchain.scribe.TypeMatcher.any;
import static eu.toolchain.scribe.TypeMatcher.isPrimitive;
import static eu.toolchain.scribe.TypeMatcher.type;

@RequiredArgsConstructor
public class TypeSafeDecoderFactory implements DecoderFactory<ConfigValue> {
  private static DecoderRegistry<ConfigValue> decoders = new DecoderRegistry<>();

  static {
    decoders.decoder(type(Map.class, any(), any()), (resolver, type, factory) -> {
      final JavaType first = type.getTypeParameter(0).get();
      final JavaType second = type.getTypeParameter(1).get();

      if (!JavaType.of(String.class).equals(first)) {
        throw new IllegalArgumentException("First type argument must be String (" + type + ")");
      }

      final Decoder<ConfigValue, Object> value =
          resolver.mapping(second).newDecoderImmediate(resolver, Flags.empty(), factory);
      return new MapDecoder<>(value);
    });

    decoders.decoder(type(List.class, any()), (resolver, type, factory) -> {
      final JavaType first = type.getTypeParameter(0).get();

      final Decoder<ConfigValue, Object> value =
          resolver.mapping(first).newDecoderImmediate(resolver, Flags.empty(), factory);

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
  public <O> Stream<Decoder<ConfigValue, O>> newDecoder(
      final EntityResolver resolver, final Flags flags, final JavaType type
  ) {
    return decoders.newDecoder(resolver, type, this);
  }

  @Override
  public Decoded<EntityFieldsDecoder<ConfigValue>> newEntityDecoder(
      final ConfigValue instance
  ) {
    final Map<String, ConfigValue> values;

    switch (instance.valueType()) {
      case OBJECT:
        values = ((ConfigObject) instance);
        break;
      case NULL:
        return Decoded.absent();
      default:
        throw new IllegalArgumentException("Expected object: " + instance);
    }

    return Decoded.of(new TypeSafeEntityFieldsDecoder(values));
  }
}
