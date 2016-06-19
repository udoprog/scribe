package eu.toolchain.ogt.entitymapper;

import eu.toolchain.ogt.EntityResolver;
import eu.toolchain.ogt.Match;
import eu.toolchain.ogt.entitybinding.EntityBinding;

import java.lang.reflect.Type;
import java.util.stream.Stream;

public interface BindingDetector {
    Stream<Match<EntityBinding>> detect(final EntityResolver resolver, final Type type);
}
