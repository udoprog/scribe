package eu.toolchain.ogt.creatormethod;

import eu.toolchain.ogt.Annotations;
import eu.toolchain.ogt.JavaType;
import lombok.Data;

import java.util.Optional;

@Data
public class CreatorField {
    private final Annotations annotations;
    private final Optional<JavaType> type;
    private final Optional<String> name;
}
