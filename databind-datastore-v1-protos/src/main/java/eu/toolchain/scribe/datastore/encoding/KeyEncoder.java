package eu.toolchain.scribe.datastore.encoding;

import com.google.datastore.v1.Key;
import com.google.datastore.v1.Value;
import eu.toolchain.scribe.Context;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class KeyEncoder extends AbstractEncoder<Key> {
  @Override
  public Value encode(final Context path, final Key instance) {
    return Value.newBuilder().setKeyValue(instance).build();
  }

  private static final KeyEncoder INSTANCE = new KeyEncoder();

  public static KeyEncoder get() {
    return INSTANCE;
  }
}
