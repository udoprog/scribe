package eu.toolchain.ogt.entitybinding;

import eu.toolchain.ogt.Context;
import eu.toolchain.ogt.fieldreader.FieldReader;

import java.util.Optional;

public interface EntityFieldEncoder<Target, Source> {
    String getName();

    FieldReader getReader();

    Target encode(Context path, Source instance);

    Optional<Object> asOptional(Source value);
}
