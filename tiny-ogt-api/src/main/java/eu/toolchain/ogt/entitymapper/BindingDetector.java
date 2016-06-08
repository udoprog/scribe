package eu.toolchain.ogt.entitymapper;

import eu.toolchain.ogt.EntityResolver;
import eu.toolchain.ogt.JavaType;
import eu.toolchain.ogt.binding.Binding;

import java.util.Optional;

public interface BindingDetector {
    Optional<Binding> detect(final EntityResolver resolver, final JavaType type);
}
