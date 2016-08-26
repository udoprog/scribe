package eu.toolchain.scribe.jackson;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import eu.toolchain.scribe.Context;
import eu.toolchain.scribe.Decoded;
import eu.toolchain.scribe.Decoder;
import eu.toolchain.scribe.Encoder;
import eu.toolchain.scribe.StreamEncoder;
import eu.toolchain.scribe.StringEncoding;
import lombok.Data;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Data
public class JacksonEncoding<Target> implements StringEncoding<Target> {
  private final Encoder<JsonNode, Target> encoder;
  private final StreamEncoder<JsonGenerator, Target> streamEncoder;
  private final Decoder<JsonNode, Target> decoder;
  private final JsonFactory json;

  public JsonNode encode(Context path, Target instance) {
    return encoder.encode(path, instance);
  }

  public void streamEncode(
      final Context path, final Target instance, final JsonGenerator generator
  ) {
    streamEncoder.streamEncode(path, instance, generator);
  }

  @SuppressWarnings("unchecked")
  public Target decode(Context path, JsonNode instance) {
    final Decoded<Target> decoded = decoder.decode(path, instance);

    if (decoded == null) {
      throw path.error("decoder returned null");
    }

    return decoded.orElseThrow(() -> path.error("input decoded to nothing"));
  }

  @Override
  public Target decodeFromString(String json) {
    try (final JsonParser parser = this.json.createParser(json)) {
      return decode(Context.ROOT, JsonNode.fromParser(parser));
    } catch (final IOException e) {
      throw new RuntimeException("Failed to generate", e);
    }
  }

  @Override
  public String encodeAsString(Target instance) {
    try (final ByteArrayOutputStream out = new ByteArrayOutputStream()) {
      try (final JsonGenerator generator = json.createGenerator(out)) {
        streamEncoder.streamEncode(Context.ROOT, instance, generator);
      }

      return new String(out.toByteArray(), StandardCharsets.UTF_8);
    } catch (final IOException e) {
      throw new RuntimeException("Failed to generate", e);
    }
  }
}
