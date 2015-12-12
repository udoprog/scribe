package eu.toolchain.ogt.binding;

import java.io.IOException;
import java.util.Optional;

import eu.toolchain.ogt.Context;
import eu.toolchain.ogt.FieldDecoder;
import eu.toolchain.ogt.EntityDecoder;
import eu.toolchain.ogt.EntityEncoder;
import eu.toolchain.ogt.fieldreader.FieldReader;
import eu.toolchain.ogt.type.TypeMapping;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TypeFieldMapping implements FieldMapping {
    private final String name;
    private final boolean indexed;
    private final TypeMapping type;
    private final FieldReader reader;

    @Override
    public String name() {
        return name;
    }

    @Override
    public boolean indexed() {
        return indexed;
    }

    public TypeMapping type() {
        return type;
    }

    public FieldReader reader() {
        return reader;
    }

    public Optional<?> decode(EntityDecoder decoder, Context path) throws IOException {
        final Optional<FieldDecoder> field = decoder.getField(this);

        if (field.isPresent()) {
            return Optional.of(type.decode(field.get(), path));
        }

        return Optional.empty();
    }

    public void encode(EntityEncoder encoder, Object value, Context path) throws IOException {
        type.encode(encoder.setField(this), value, path);
    }

    @Override
    public String toString() {
        return name + "=" + type;
    }
}
