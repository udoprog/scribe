package eu.toolchain.ogt;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

public class JacksonEncodingFactory implements EncodingFactory<String> {
    private final JsonFactory jsonFactory;

    public JacksonEncodingFactory(JsonFactory jsonFactory) {
        this.jsonFactory = jsonFactory;
    }

    @Override
    public EntityEncoder entityEncoder() {
        return new JacksonEntityEncoder() {
            @Override
            public Object encode() {
                final JsonNode node = (JsonNode) super.encode();

                try {
                    final StringWriter writer = new StringWriter();

                    try (final JsonGenerator generator = jsonFactory.createGenerator(writer)) {
                        node.generate(generator);
                        return writer.toString();
                    }
                } catch (final IOException e) {
                    throw new RuntimeException("Failed to parse JSON", e);
                }
            }
        };
    }

    @Override
    public EntityDecoder entityDecoder(final String input) {
        final JsonNode node;

        final StringReader reader = new StringReader(input);

        try {
            try (final JsonParser parser = jsonFactory.createParser(reader)) {
                node = JsonNode.fromParser(parser);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return new JacksonEntityDecoder(node);
    }

    @Override
    public FieldEncoder<?> fieldEncoder() {
        return new JacksonFieldEncoder() {
            @Override
            public Object encode(final JsonNode value) {
                try {
                    final StringWriter writer = new StringWriter();

                    try (final JsonGenerator generator = jsonFactory.createGenerator(writer)) {
                        value.generate(generator);
                    }

                    return writer.toString();
                } catch (final IOException e) {
                    throw new RuntimeException("Failed to parse JSON", e);
                }
            }
        };
    }

    @Override
    public FieldDecoder<?> fieldDecoder() {
        return new JacksonFieldDecoder() {
            @Override
            public JsonNode decode(final Object value) {
                final StringReader reader = new StringReader((String) value);

                try {
                    try (final JsonParser parser = jsonFactory.createParser(reader)) {
                        return JsonNode.fromParser(parser);
                    }
                } catch (final IOException e) {
                    throw new RuntimeException("Failed to parse JSON", e);
                }
            }
        };
    }
}
