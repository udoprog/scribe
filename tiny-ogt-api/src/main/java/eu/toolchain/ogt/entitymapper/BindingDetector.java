package eu.toolchain.ogt.entitymapper;

import eu.toolchain.ogt.EntityResolver;
import eu.toolchain.ogt.type.JavaType;
import eu.toolchain.ogt.Match;
import eu.toolchain.ogt.entitybinding.EntityBinding;

import java.util.stream.Stream;

public interface BindingDetector {
    Stream<Match<EntityBinding>> detect(final EntityResolver resolver, final JavaType type);
}
