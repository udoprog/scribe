package eu.toolchain.ogt.binding;

import eu.toolchain.ogt.type.TypeMapping;

public interface FieldMapping {
    public String name();

    public boolean indexed();

    public TypeMapping type();
}
