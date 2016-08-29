package eu.toolchain.scribe.datastore.encoding;

import eu.toolchain.scribe.Context;
import com.google.datastore.v1.Value;
import lombok.Data;

@Data
public class IntegerEncoder extends AbstractEncoder<Integer> {
  @Override
  public Value encode(final Context path, final Integer instance) {
    return Value.newBuilder().setIntegerValue(instance).build();
  }

  private static final IntegerEncoder INSTANCE = new IntegerEncoder();

  public static IntegerEncoder get() {
    return INSTANCE;
  }
}
