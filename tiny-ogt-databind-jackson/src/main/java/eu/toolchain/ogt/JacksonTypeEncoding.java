package eu.toolchain.ogt;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.google.common.base.Charsets;
import lombok.Data;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Data
public class JacksonTypeEncoding<T> {
    private final Encoder<JsonNode, T> parentEncoder;
    private final Decoder<JsonNode, T> parentDecoder;
    private final JsonFactory json;

    public JsonNode encode(T instance) {
        return parentEncoder.encode(Context.ROOT, instance);
    }

    public T encode(JsonNode instance) {
        return parentDecoder.decode(Context.ROOT, instance);
    }

    public T decodeFromString(String json) {
        try (final JsonParser parser = this.json.createParser(json)) {
            return parentDecoder.decode(Context.ROOT, JsonNode.fromParser(parser));
        } catch (final IOException e) {
            throw new RuntimeException("Failed to generate", e);
        }
    }

    public String encodeAsString(T instance) {
        final JsonNode node = parentEncoder.encode(Context.ROOT, instance);

        try (final ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            final JsonGenerator generator = json.createGenerator(out);
            node.generate(generator);
            generator.close();
            return new String(out.toByteArray(), Charsets.UTF_8);
        } catch (final IOException e) {
            throw new RuntimeException("Failed to generate", e);
        }
    }
}
