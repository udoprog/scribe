package eu.toolchain.scribe.datastore;

import com.google.datastore.v1.Value;

import eu.toolchain.scribe.Context;
import eu.toolchain.scribe.Decoded;
import eu.toolchain.scribe.Decoder;
import eu.toolchain.scribe.Encoder;

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
}
