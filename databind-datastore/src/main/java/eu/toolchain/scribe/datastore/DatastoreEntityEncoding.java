package eu.toolchain.scribe.datastore;

import com.google.datastore.v1.Entity;
import com.google.datastore.v1.Value;
import eu.toolchain.scribe.Context;
import eu.toolchain.scribe.EntityDecoder;
import eu.toolchain.scribe.EntityEncoder;
import lombok.Data;

@Data
public class DatastoreEntityEncoding<Source> {
  private final EntityEncoder<Value, Entity, Source> encoder;
  private final EntityDecoder<Value, Entity, Source> decoder;

  public Entity encodeEntity(Source instance) {
    return encoder.encodeEntity(Context.ROOT, instance);
  }

  public Source decodeEntity(Entity instance) {
    final Source decoded = decoder.decodeEntity(Context.ROOT, instance);

    if (decoded == null) {
      throw Context.ROOT.error("decoder returned null");
    }

    return decoded;
  }
}
