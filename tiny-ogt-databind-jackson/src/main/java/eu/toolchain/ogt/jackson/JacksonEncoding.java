package eu.toolchain.ogt.jackson;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.google.common.base.Charsets;
import eu.toolchain.ogt.Context;
import eu.toolchain.ogt.Decoder;
import eu.toolchain.ogt.Encoder;
import eu.toolchain.ogt.Encoding;
import eu.toolchain.ogt.StringEncoding;
import lombok.Data;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Data
public class JacksonEncoding<T> implements StringEncoding<T>, Encoding<JsonNode, T> {
    private final Encoder<JsonNode, T> parentEncoder;
    private final Decoder<JsonNode, T> parentDecoder;
    private final JsonFactory json;

    @Override
    public JsonNode encode(T instance) {
        return parentEncoder.encode(Context.ROOT, instance);
    }

    @Override
    public T decode(JsonNode instance) {
        return parentDecoder.decode(Context.ROOT, instance);
    }

    @Override
    public T decodeFromString(String json) {
        try (final JsonParser parser = this.json.createParser(json)) {
            return parentDecoder.decode(Context.ROOT, JsonNode.fromParser(parser));
        } catch (final IOException e) {
            throw new RuntimeException("Failed to generate", e);
        }
    }

    @Override
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
