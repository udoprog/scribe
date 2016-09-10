package eu.toolchain.scribe.datastore.encoding;

import com.google.datastore.v1.Value;
import eu.toolchain.scribe.Context;
import eu.toolchain.scribe.Decoded;
import eu.toolchain.scribe.Decoder;
import lombok.Data;

@Data
public class ExcludeFromIndexesDecoder<Source> implements Decoder<Value, Source> {
  private final Decoder<Value, Source> parent;

  @Override
  public Decoded<Source> decode(final Context path, final Value instance) {
    if (!instance.getExcludeFromIndexes()) {
      throw path.error("attempting to decode value that is not excluded from indexes");
    }

    return parent.decode(path, instance);
  }
}
