package eu.toolchain.ogt.entitymapper;

import eu.toolchain.ogt.EntityResolver;
import eu.toolchain.ogt.JavaType;
import eu.toolchain.ogt.binding.EntityBinding;

import java.util.Optional;

public interface BindingDetector {
    Optional<EntityBinding> detect(final EntityResolver resolver, final JavaType type);
}
