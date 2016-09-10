package eu.toolchain.scribe.datastore.encoding;

import com.google.datastore.v1.Value;

import eu.toolchain.scribe.Context;
import eu.toolchain.scribe.Decoded;
import eu.toolchain.scribe.Decoder;

import lombok.Data;

@Data
public class BooleanDecoder implements Decoder<Value, Boolean> {
  @Override
  public Decoded<Boolean> decode(final Context path, final Value instance) {
    switch (instance.getValueTypeCase()) {
      case BOOLEAN_VALUE:
        return Decoded.of(instance.getBooleanValue());
      case NULL_VALUE:
        return Decoded.absent();
      default:
        throw path.error("expected boolean");
    }
  }

  private static final BooleanDecoder INSTANCE = new BooleanDecoder();

  public static BooleanDecoder get() {
    return INSTANCE;
  }
}
