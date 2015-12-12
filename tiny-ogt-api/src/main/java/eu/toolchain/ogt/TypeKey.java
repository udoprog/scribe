package eu.toolchain.ogt;

import java.util.Optional;

import lombok.Data;

@Data
public class TypeKey {
    private final String kind;
    private final Optional<TypeKey> parent;
}
