package eu.toolchain.scribe.datastore.encoding;

import com.google.datastore.v1.Value;

import eu.toolchain.scribe.Context;
import eu.toolchain.scribe.Decoded;
import eu.toolchain.scribe.Decoder;

import java.util.function.Function;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class NumberDecoder<T extends Number> implements Decoder<Value, T> {
  private final Function<Number, T> converter;

  @Override
  public Decoded<T> decode(final Context path, final Value instance) {
    switch (instance.getValueTypeCase()) {
      case DOUBLE_VALUE:
        return Decoded.of(converter.apply(instance.getDoubleValue()));
      case INTEGER_VALUE:
        return Decoded.of(converter.apply(instance.getIntegerValue()));
      case NULL_VALUE:
        return Decoded.absent();
      default:
        throw path.error("expected numerical value");
    }
  }

  public static final NumberDecoder<Short> SHORT = new NumberDecoder<>(Number::shortValue);
  public static final NumberDecoder<Integer> INTEGER = new NumberDecoder<>(Number::intValue);
  public static final NumberDecoder<Long> LONG = new NumberDecoder<>(Number::longValue);
  public static final NumberDecoder<Float> FLOAT = new NumberDecoder<>(Number::floatValue);
  public static final NumberDecoder<Double> DOUBLE = new NumberDecoder<>(Number::doubleValue);
}
