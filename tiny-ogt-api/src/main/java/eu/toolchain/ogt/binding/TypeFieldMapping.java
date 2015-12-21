package eu.toolchain.ogt.binding;

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

    @Override
    public TypeMapping type() {
        return type;
    }

    public FieldReader reader() {
        return reader;
    }

    @Override
    public String toString() {
        return name + "=" + type;
    }
}
