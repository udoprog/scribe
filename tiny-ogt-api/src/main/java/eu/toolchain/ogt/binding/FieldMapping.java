package eu.toolchain.ogt.binding;

import eu.toolchain.ogt.fieldreader.FieldReader;
import eu.toolchain.ogt.type.TypeMapping;

public interface FieldMapping {
    String name();

    TypeMapping type();

    FieldReader reader();
}
