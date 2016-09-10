package eu.toolchain.scribe.datastore.encoding;

import com.google.datastore.v1.Value;

import eu.toolchain.scribe.Context;

import lombok.Data;

@Data
public class DoubleEncoder extends AbstractEncoder<Double> {
  @Override
  public Value encode(final Context path, final Double instance) {
    return Value.newBuilder().setDoubleValue(instance).build();
  }

  private static final DoubleEncoder INSTANCE = new DoubleEncoder();

  public static DoubleEncoder get() {
    return INSTANCE;
  }
}
