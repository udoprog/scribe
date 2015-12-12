package eu.toolchain.ogt.type;

import java.io.IOException;
import java.util.List;

import eu.toolchain.ogt.Context;
import eu.toolchain.ogt.EntityResolver;
import eu.toolchain.ogt.FieldDecoder;
import eu.toolchain.ogt.FieldEncoder;
import eu.toolchain.ogt.JavaType;
import lombok.Data;

@Data
public class ListTypeMapping implements TypeMapping {
    private final JavaType javaType;
    private final TypeMapping value;

    @Override
    public String toString() {
        return "list<" + value + ">";
    }

    @Override
    public JavaType getType() {
        return javaType;
    }

    @Override
    public Object decode(FieldDecoder accessor, Context path) throws IOException {
        return accessor.asList(value, path);
    }

    @Override
    public void encode(FieldEncoder visitor, Object list, Context path) throws IOException {
        visitor.setList(value, (List<?>) list, path);
    }

    @Override
    public void initialize(final EntityResolver resolver) {
        value.initialize(resolver);
    }
}
