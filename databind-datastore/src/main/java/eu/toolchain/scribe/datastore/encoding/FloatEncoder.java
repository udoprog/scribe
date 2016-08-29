package eu.toolchain.scribe.datastore.encoding;

import com.google.datastore.v1.Value;

import eu.toolchain.scribe.Context;

import lombok.Data;

@Data
public class FloatEncoder extends AbstractEncoder<Float> {
  @Override
  public Value encode(final Context path, final Float instance) {
    return Value.newBuilder().setDoubleValue(instance).build();
  }

  private static final FloatEncoder INSTANCE = new FloatEncoder();

  public static FloatEncoder get() {
    return INSTANCE;
  }
}
