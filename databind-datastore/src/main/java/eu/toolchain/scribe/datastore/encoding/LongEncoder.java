package eu.toolchain.scribe.datastore.encoding;

import com.google.datastore.v1.Value;

import eu.toolchain.scribe.Context;

import lombok.Data;

@Data
public class LongEncoder extends AbstractEncoder<Long> {
  @Override
  public Value encode(final Context path, final Long instance) {
    return Value.newBuilder().setIntegerValue(instance).build();
  }

  private static final LongEncoder INSTANCE = new LongEncoder();

  public static LongEncoder get() {
    return INSTANCE;
  }
}
