package eu.toolchain.scribe.datastore.encoding;

import com.google.datastore.v1.Value;

import eu.toolchain.scribe.Context;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class BooleanEncoder extends AbstractEncoder<Boolean> {
  @Override
  public Value encode(final Context path, final Boolean instance) {
    return Value.newBuilder().setBooleanValue(instance).build();
  }

  private static final BooleanEncoder INSTANCE = new BooleanEncoder();

  public static BooleanEncoder get() {
    return INSTANCE;
  }
}
