package eu.toolchain.ogt;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.google.common.base.Charsets;
import lombok.RequiredArgsConstructor;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@RequiredArgsConstructor
public class JacksonTypeEncoding<T> {
    private final TypeEncoding<T, JsonNode> parent;
    private final JsonFactory json;

    public JsonNode encode(T instance) {
        return parent.encode(instance);
    }

    public T decode(JsonNode instance) {
        return parent.decode(instance);
    }

    public T decodeFromString(String json) {
        try (final JsonParser parser = this.json.createParser(json)) {
            return parent.decode(JsonNode.fromParser(parser));
        } catch(final IOException e) {
            throw new RuntimeException("Failed to generate", e);
        }
    }

    public String encodeAsString(T instance) {
        final JsonNode node = parent.encode(instance);

        try (final ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            final JsonGenerator generator = json.createGenerator(out);
            node.generate(generator);
            generator.close();
            return new String(out.toByteArray(), Charsets.UTF_8);
        } catch(final IOException e) {
            throw new RuntimeException("Failed to generate", e);
        }
    }
}
