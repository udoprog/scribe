package eu.toolchain.scribe.jackson.encoding;

import eu.toolchain.scribe.Context;
import eu.toolchain.scribe.Decoded;
import eu.toolchain.scribe.Decoder;
import eu.toolchain.scribe.jackson.JsonNode;
import lombok.RequiredArgsConstructor;

import java.util.function.Function;

@RequiredArgsConstructor
public class NumberDecoder<T extends Number> extends AbstractVisitor<T>
    implements Decoder<JsonNode, T> {
  private final Function<Number, T> converter;

  @Override
  public Decoded<T> visitNumber(final JsonNode.NumberJsonNode numberNode) {
    return Decoded.of(converter.apply(numberNode.getValue()));
  }

  @Override
  public Decoded<T> visitFloat(final JsonNode.FloatJsonNode floatNode) {
    return Decoded.of(converter.apply(floatNode.getValue()));
  }

  @Override
  public Decoded<T> decode(final Context path, final JsonNode instance) {
    return instance.visit(this);
  }

  public static final NumberDecoder<Short> SHORT = new NumberDecoder<>(Number::shortValue);
  public static final NumberDecoder<Integer> INTEGER = new NumberDecoder<>(Number::intValue);
  public static final NumberDecoder<Long> LONG = new NumberDecoder<>(Number::longValue);
  public static final NumberDecoder<Float> FLOAT = new NumberDecoder<>(Number::floatValue);
  public static final NumberDecoder<Double> DOUBLE = new NumberDecoder<>(Number::doubleValue);
}
