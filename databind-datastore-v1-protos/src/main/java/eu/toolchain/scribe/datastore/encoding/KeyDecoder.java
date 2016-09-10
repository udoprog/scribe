package eu.toolchain.scribe.datastore.encoding;

import com.google.datastore.v1.Key;
import com.google.datastore.v1.Value;
import eu.toolchain.scribe.Context;
import eu.toolchain.scribe.Decoded;
import eu.toolchain.scribe.Decoder;
import lombok.Data;

@Data
public class KeyDecoder implements Decoder<Value, Key> {
  @Override
  public Decoded<Key> decode(final Context path, final Value instance) {
    switch (instance.getValueTypeCase()) {
      case KEY_VALUE:
        return Decoded.of(instance.getKeyValue());
      case NULL_VALUE:
        return Decoded.absent();
      default:
        throw path.error("expected key");
    }
  }

  private static final KeyDecoder INSTANCE = new KeyDecoder();

  public static KeyDecoder get() {
    return INSTANCE;
  }
}
