package eu.toolchain.ogt.entitymapper;

import java.util.Optional;

import eu.toolchain.ogt.EntityResolver;
import eu.toolchain.ogt.JavaType;
import eu.toolchain.ogt.binding.Binding;

public interface BindingDetector {
    Optional<Binding> detect(final EntityResolver resolver, final JavaType type);
}
