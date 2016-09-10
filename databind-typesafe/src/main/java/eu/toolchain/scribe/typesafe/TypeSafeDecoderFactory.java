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
import eu.toolchain.scribe.reflection.JavaType;
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
public class TypeSafeDecoderFactory implements DecoderFactory<ConfigValue, ConfigObject> {
  private static DecoderRegistry<ConfigValue, ConfigObject> decoders = new DecoderRegistry<>();

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

    decoders.setup(type(String.class), (resolver, type, factory) -> Stream.of(StringDecoder.get()));

    decoders.setup(isPrimitive(Boolean.class),
        (resolver, type, factory) -> Stream.of(BooleanDecoder.get()));

    decoders.setup(isPrimitive(Short.class),
        (resolver, type, factory) -> Stream.of(NumberDecoder.SHORT));
    decoders.setup(isPrimitive(Integer.class),
        (resolver, type, factory) -> Stream.of(NumberDecoder.INTEGER));
    decoders.setup(isPrimitive(Long.class),
        (resolver, type, factory) -> Stream.of(NumberDecoder.LONG));
    decoders.setup(isPrimitive(Float.class),
        (resolver, type, factory) -> Stream.of(NumberDecoder.FLOAT));
    decoders.setup(isPrimitive(Double.class),
        (resolver, type, factory) -> Stream.of(NumberDecoder.DOUBLE));
  }

  @Override
  public <O> Stream<Decoder<ConfigValue, O>> newDecoder(
      final EntityResolver resolver, final JavaType type, final Flags flags
  ) {
    return decoders.newDecoder(resolver, type, this);
  }

  @Override
  public EntityFieldsDecoder<ConfigValue> newEntityDecoder(
      final ConfigObject instance
  ) {
    return new TypeSafeEntityFieldsDecoder(instance);
  }

  @Override
  public Decoded<ConfigObject> valueAsEntity(final ConfigValue instance) {
    switch (instance.valueType()) {
      case OBJECT:
        return Decoded.of(((ConfigObject) instance));
      case NULL:
        return Decoded.absent();
      default:
        throw new IllegalArgumentException("Expected object: " + instance);
    }
  }
}
