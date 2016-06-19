package eu.toolchain.ogt.entitybinding;

import eu.toolchain.ogt.EncodingFactory;
import eu.toolchain.ogt.EntityResolver;
import eu.toolchain.ogt.EntityTypeDecoder;
import eu.toolchain.ogt.EntityTypeEncoder;

import java.util.List;

public interface EntityBinding {
    List<? extends EntityFieldMapping> fields();

    <Target> EntityTypeEncoder<Target, Object> newEntityTypeEncoder(
        EntityResolver resolver, EncodingFactory<Target> factory
    );

    <Target> EntityTypeDecoder<Target, Object> newEntityTypeDecoder(
        EntityResolver resolver, EncodingFactory<Target> factory
    );
}
