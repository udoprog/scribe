package eu.toolchain.ogt;

import lombok.Data;

import java.util.Optional;

@Data
public class TypeKey {
    private final String kind;
    private final Optional<TypeKey> parent;
}
