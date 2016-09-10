package eu.toolchain.scribe.datastore;

import com.google.datastore.v1.Entity;
import com.google.datastore.v1.Value;
import eu.toolchain.scribe.Context;
import eu.toolchain.scribe.Decoded;
import eu.toolchain.scribe.Decoder;
import eu.toolchain.scribe.Encoder;
import eu.toolchain.scribe.EntityDecoder;
import eu.toolchain.scribe.EntityEncoder;
import lombok.Data;

@Data
public class DatastoreEncoding<Source> {
  private final Encoder<Value, Source> encoder;
  private final Decoder<Value, Source> decoder;

  public Value encode(Source instance) {
    return encoder.encode(Context.ROOT, instance);
  }

  public Source decode(Value instance) {
    final Decoded<Source> decoded = decoder.decode(Context.ROOT, instance);

    if (decoded == null) {
      throw Context.ROOT.error("decoder returned null");
    }

    return decoded.orElseThrow(() -> Context.ROOT.error("input decoded to nothing"));
  }

  public DatastoreEntityEncoding<Source> asEntityEncoding() {
    if (!(encoder instanceof EntityEncoder)) {
      throw new IllegalStateException("Encoder is not for entities");
    }

    if (!(decoder instanceof EntityDecoder)) {
      throw new IllegalStateException("Decoder is not for entities");
    }

    return new DatastoreEntityEncoding<>((EntityEncoder<Value, Entity, Source>) encoder,
        (EntityDecoder<Value, Entity, Source>) decoder);
  }
}
