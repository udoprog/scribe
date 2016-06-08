package eu.toolchain.ogt.creatormethod;

import eu.toolchain.ogt.Annotations;
import eu.toolchain.ogt.JavaType;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@RequiredArgsConstructor
public class CreatorField {
    private final Annotations annotations;
    private final Optional<JavaType> type;
    private final Optional<String> name;

    public Annotations annotations() {
        return annotations;
    }

    public Optional<JavaType> type() {
        return type;
    }

    public Optional<String> name() {
        return name;
    }
}
