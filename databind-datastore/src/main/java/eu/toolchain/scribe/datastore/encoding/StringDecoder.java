package eu.toolchain.scribe.datastore.encoding;

import com.google.datastore.v1.Value;

import eu.toolchain.scribe.Context;
import eu.toolchain.scribe.Decoded;
import eu.toolchain.scribe.Decoder;

import lombok.Data;

@Data
public class StringDecoder implements Decoder<Value, String> {
  @Override
  public Decoded<String> decode(final Context path, final Value instance) {
    switch (instance.getValueTypeCase()) {
      case STRING_VALUE:
        return Decoded.of(instance.getStringValue());
      case NULL_VALUE:
        return Decoded.absent();
      default:
        throw path.error("expected string");
    }
  }

  private static final StringDecoder INSTANCE = new StringDecoder();

  public static StringDecoder get() {
    return INSTANCE;
  }
}
