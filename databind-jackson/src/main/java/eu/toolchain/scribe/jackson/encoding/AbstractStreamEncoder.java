package eu.toolchain.scribe.jackson.encoding;

import com.fasterxml.jackson.core.JsonGenerator;
import eu.toolchain.scribe.Context;
import eu.toolchain.scribe.StreamEncoder;
import lombok.EqualsAndHashCode;

import java.io.IOException;

@EqualsAndHashCode
public abstract class AbstractStreamEncoder<T> implements StreamEncoder<JsonGenerator, T> {
  @Override
  public void streamEncodeEmpty(
      final Context path, final JsonGenerator generator
  ) {
    try {
      generator.writeNull();
    } catch (final IOException e) {
      throw path.error("failed to encode empty", e);
    }
  }
}
