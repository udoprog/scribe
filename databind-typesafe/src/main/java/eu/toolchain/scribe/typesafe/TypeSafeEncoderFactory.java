package eu.toolchain.scribe.typesafe;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigValue;
import eu.toolchain.scribe.Encoder;
import eu.toolchain.scribe.EncoderFactory;
import eu.toolchain.scribe.EncoderRegistry;
import eu.toolchain.scribe.EntityFieldsEncoder;
import eu.toolchain.scribe.EntityResolver;
import eu.toolchain.scribe.Flags;
import eu.toolchain.scribe.reflection.JavaType;
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
public class TypeSafeEncoderFactory implements EncoderFactory<ConfigValue, Config> {
  private static EncoderRegistry<ConfigValue, Config> encoders = new EncoderRegistry<>();

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

    encoders.setup(type(String.class), (resolver, type, factory) -> Stream.of(StringEncoder.get()));

    encoders.setup(isPrimitive(Boolean.class),
        (resolver, type, factory) -> Stream.of(BooleanEncoder.get()));

    encoders.setup(
        anyOf(isPrimitive(Short.class), isPrimitive(Integer.class), isPrimitive(Long.class),
            isPrimitive(Float.class), isPrimitive(Double.class)),
        (resolver, type, factory) -> Stream.of(NumberEncoder.get()));
  }

  @Override
  public <O> Stream<Encoder<ConfigValue, O>> newEncoder(
      final EntityResolver resolver, final JavaType type, final Flags flags
  ) {
    return encoders.newEncoder(resolver, type, this);
  }

  @Override
  public EntityFieldsEncoder<ConfigValue, Config> newEntityEncoder() {
    return new TypeSafeEntityFieldsEncoder();
  }

  @Override
  public ConfigValue entityAsValue(final Config entity) {
    return entity.root();
  }
}
