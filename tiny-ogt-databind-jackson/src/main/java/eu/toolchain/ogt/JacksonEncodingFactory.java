package eu.toolchain.ogt;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Optional;

import eu.toolchain.ogt.binding.FieldMapping;

public class JacksonEncodingFactory implements EncodingFactory<String> {
    private final JsonFactory jsonFactory;

    public JacksonEncodingFactory(JsonFactory jsonFactory) {
        this.jsonFactory = jsonFactory;
    }

    @Override
    public BuildableEntityEncoder<String> entityEncoder() {
        final StringWriter writer = new StringWriter();
        final JsonGenerator generator;

        try {
            generator = jsonFactory.createGenerator(writer);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }

        final EntityEncoder encoder = new JacksonEntityEncoder(generator);

        return new BuildableEntityEncoder<String>() {
            @Override
            public void startEntity() throws IOException {
                encoder.startEntity();
            }

            @Override
            public void endEntity() throws IOException {
                encoder.endEntity();
            }

            @Override
            public void setType(final String type) throws IOException {
                encoder.setType(type);
            }

            @Override
            public FieldEncoder setField(final FieldMapping field) throws IOException {
                return encoder.setField(field);
            }

            @Override
            public String build() {
                try {
                    generator.close();
                } catch (final IOException e) {
                    throw new RuntimeException(e);
                }

                return writer.toString();
            }
        };
    }

    @Override
    public EntityDecoder entityDecoder(final String input) {
        final StringReader reader = new StringReader(input);

        final JsonParser parser;

        try {
            parser = jsonFactory.createParser(reader);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        final EntityDecoder decoder;

        try {
            decoder = new JacksonEntityDecoder(JsonNode.fromParser(parser));
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }

        return new EntityDecoder() {
            @Override
            public Optional<FieldDecoder> getField(FieldMapping field) {
                return decoder.getField(field);
            }

            @Override
            public Optional<String> getType() {
                return decoder.getType();
            }

            @Override
            public void end() {
                try {
                    parser.close();
                } catch (final IOException e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }
}
