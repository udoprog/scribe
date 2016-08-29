package eu.toolchain.scribe.datastore.encoding;

import com.google.datastore.v1.Value;

import eu.toolchain.scribe.Context;

import lombok.Data;

@Data
public class StringEncoder extends AbstractEncoder<String> {
  @Override
  public Value encode(final Context path, final String instance) {
    return Value.newBuilder().setStringValue(instance).build();
  }

  private static final StringEncoder INSTANCE = new StringEncoder();

  public static StringEncoder get() {
    return INSTANCE;
  }
}
