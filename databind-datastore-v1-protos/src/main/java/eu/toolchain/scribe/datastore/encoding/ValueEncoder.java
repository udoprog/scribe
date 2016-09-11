package eu.toolchain.scribe.datastore.encoding;

import com.google.datastore.v1.Value;
import eu.toolchain.scribe.Context;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ValueEncoder extends AbstractEncoder<Value> {
  @Override
  public Value encode(final Context path, final Value instance) {
    return instance;
  }

  private static final ValueEncoder INSTANCE = new ValueEncoder();

  public static ValueEncoder get() {
    return INSTANCE;
  }
}
