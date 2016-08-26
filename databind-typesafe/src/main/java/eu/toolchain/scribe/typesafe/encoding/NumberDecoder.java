package eu.toolchain.scribe.typesafe.encoding;

import com.typesafe.config.ConfigValue;
import com.typesafe.config.ConfigValueType;
import eu.toolchain.scribe.Context;
import eu.toolchain.scribe.Decoded;
import eu.toolchain.scribe.Decoder;
import lombok.Data;

import java.util.function.Function;

@Data
public class NumberDecoder<T extends Number> implements Decoder<ConfigValue, T> {
  private final Function<Number, T> converter;

  @Override
  public Decoded<T> decode(final Context path, final ConfigValue instance) {
    if (instance.valueType() == ConfigValueType.NULL) {
      return Decoded.absent();
    }

    if (instance.valueType() != ConfigValueType.NUMBER) {
      throw new IllegalArgumentException("Expected number: " + instance);
    }

    return Decoded.of(converter.apply(((Number) instance.unwrapped())));
  }

  public static final NumberDecoder<Short> SHORT = new NumberDecoder<>(Number::shortValue);
  public static final NumberDecoder<Integer> INTEGER = new NumberDecoder<>(Number::intValue);
  public static final NumberDecoder<Long> LONG = new NumberDecoder<>(Number::longValue);
  public static final NumberDecoder<Float> FLOAT = new NumberDecoder<>(Number::floatValue);
  public static final NumberDecoder<Double> DOUBLE = new NumberDecoder<>(Number::doubleValue);
}
