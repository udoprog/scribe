package eu.toolchain.ogt.entitybinding;

import eu.toolchain.ogt.Context;

import java.util.Optional;

public interface EntityFieldDecoder<Target, Source> {
    String getName();

    Source decode(Context path, Target instance);

    Optional<?> fromOptional(Optional<?> value);
}
