package eu.toolchain.ogt;

import com.fasterxml.jackson.core.JsonGenerator;

import java.io.IOException;

import eu.toolchain.ogt.binding.FieldMapping;

public class JacksonEntityEncoder implements EntityEncoder {
    private final JsonGenerator generator;

    public JacksonEntityEncoder(final JsonGenerator generator) {
        this.generator = generator;
    }

    @Override
    public void startEntity() throws IOException {
        generator.writeStartObject();
    }

    @Override
    public void endEntity() throws IOException {
        generator.writeEndObject();
    }

    @Override
    public void setType(String type) throws IOException {
        generator.writeStringField("type", type);
    }

    @Override
    public FieldEncoder setField(FieldMapping field) throws IOException {
        generator.writeFieldName(field.name());
        return new JacksonFieldEncoder(generator);
    }
}
