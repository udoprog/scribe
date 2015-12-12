package eu.toolchain.ogt.type;

import java.io.IOException;
import java.util.Map;

import eu.toolchain.ogt.Context;
import eu.toolchain.ogt.EntityResolver;
import eu.toolchain.ogt.FieldDecoder;
import eu.toolchain.ogt.FieldEncoder;
import eu.toolchain.ogt.JavaType;
import lombok.Data;

@Data
public class MapTypeMapping implements TypeMapping {
    private final JavaType javaType;
    private final TypeMapping key;
    private final TypeMapping value;

    @Override
    public String toString() {
        return "map<" + key + "=" + value + ">";
    }

    @Override
    public JavaType getType() {
        return javaType;
    }

    @Override
    public Object decode(FieldDecoder accessor, Context path) throws IOException {
        return accessor.asMap(key, value, path);
    }

    @Override
    public void encode(FieldEncoder visitor, Object map, Context path) throws IOException {
        visitor.setMap(key, value, (Map<?, ?>) map, path);
    }

    @Override
    public void initialize(final EntityResolver resolver) {
        key.initialize(resolver);
        value.initialize(resolver);
    }
}
