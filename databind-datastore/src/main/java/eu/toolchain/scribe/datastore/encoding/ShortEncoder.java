package eu.toolchain.scribe.datastore.encoding;

import com.google.datastore.v1.Value;

import eu.toolchain.scribe.Context;

import lombok.Data;

@Data
public class ShortEncoder extends AbstractEncoder<Short> {
  @Override
  public Value encode(final Context path, final Short instance) {
    return Value.newBuilder().setIntegerValue(instance).build();
  }

  private static final ShortEncoder INSTANCE = new ShortEncoder();

  public static ShortEncoder get() {
    return INSTANCE;
  }
}
