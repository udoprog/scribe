package eu.toolchain.scribe.typesafe;

import com.typesafe.config.ConfigValue;
import eu.toolchain.scribe.Encoder;
import eu.toolchain.scribe.EncoderFactory;
import eu.toolchain.scribe.EncoderRegistry;
import eu.toolchain.scribe.EntityFieldsEncoder;
import eu.toolchain.scribe.EntityResolver;
import eu.toolchain.scribe.Flags;
import eu.toolchain.scribe.JavaType;
import eu.toolchain.scribe.typesafe.encoding.BooleanEncoder;
import eu.toolchain.scribe.typesafe.encoding.ListEncoder;
import eu.toolchain.scribe.typesafe.encoding.MapEncoder;
import eu.toolchain.scribe.typesafe.encoding.NumberEncoder;
import eu.toolchain.scribe.typesafe.encoding.StringEncoder;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static eu.toolchain.scribe.TypeMatcher.any;
import static eu.toolchain.scribe.TypeMatcher.anyOf;
import static eu.toolchain.scribe.TypeMatcher.isPrimitive;
import static eu.toolchain.scribe.TypeMatcher.type;

@RequiredArgsConstructor
public class TypeSafeEncoderFactory implements EncoderFactory<ConfigValue> {
  private static EncoderRegistry<ConfigValue> encoders = new EncoderRegistry<>();

  static {
    encoders.encoder(type(Map.class, any(), any()), (resolver, type, factory) -> {
      final JavaType first = type.getTypeParameter(0).get();
      final JavaType second = type.getTypeParameter(1).get();

      if (!JavaType.of(String.class).equals(first)) {
        throw new IllegalArgumentException("First type argument must be String (" + type + ")");
      }

      final Encoder<ConfigValue, Object> value =
          resolver.mapping(second).newEncoderImmediate(resolver, Flags.empty(), factory);
      return new MapEncoder<>(value);
    });

    encoders.encoder(type(List.class, any()), (resolver, type, factory) -> {
      final JavaType first = type.getTypeParameter(0).get();

      final Encoder<ConfigValue, Object> value =
          resolver.mapping(first).newEncoderImmediate(resolver, Flags.empty(), factory);

      return new ListEncoder<>(value);
    });

    encoders.encoder(type(String.class), (resolver, type, factory) -> StringEncoder.get());

    encoders.encoder(isPrimitive(Boolean.class), (resolver, type, factory) -> BooleanEncoder.get());

    encoders.encoder(
        anyOf(isPrimitive(Short.class), isPrimitive(Integer.class), isPrimitive(Long.class),
            isPrimitive(Float.class), isPrimitive(Double.class)),
        (resolver, type, factory) -> NumberEncoder.get());
  }

  @Override
  public <O> Stream<Encoder<ConfigValue, O>> newEncoder(
      final EntityResolver resolver, final Flags flags, final JavaType type
  ) {
    return encoders.newEncoder(resolver, type, this);
  }

  @Override
  public EntityFieldsEncoder<ConfigValue> newEntityEncoder() {
    return new TypeSafeEntityFieldsEncoder();
  }
}
