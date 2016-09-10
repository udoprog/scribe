package eu.toolchain.scribe.datastore.encoding;

import com.google.datastore.v1.Value;
import com.google.protobuf.NullValue;
import eu.toolchain.scribe.Context;
import eu.toolchain.scribe.Encoder;

public abstract class AbstractEncoder<Source> implements Encoder<Value, Source> {
  @Override
  public Value encodeEmpty(final Context path) {
    return Value.newBuilder().setNullValue(NullValue.NULL_VALUE).build();
  }
}
