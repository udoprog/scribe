package eu.toolchain.scribe.datastore.encoding;

import com.google.datastore.v1.Value;
import eu.toolchain.scribe.Context;
import eu.toolchain.scribe.Encoder;
import lombok.Data;

@Data
public class ExcludeFromIndexesEncoder<Source> implements Encoder<Value, Source> {
  private final Encoder<Value, Source> parent;

  @Override
  public Value encode(final Context path, final Source instance) {
    return parent.encode(path, instance).toBuilder().setExcludeFromIndexes(true).build();
  }

  @Override
  public Value encodeEmpty(final Context path) {
    return parent.encodeEmpty(path).toBuilder().setExcludeFromIndexes(true).build();
  }
}
