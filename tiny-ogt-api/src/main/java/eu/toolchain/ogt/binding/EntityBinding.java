package eu.toolchain.ogt.binding;

import eu.toolchain.ogt.Context;
import eu.toolchain.ogt.EntityDecoder;
import eu.toolchain.ogt.EntityEncoder;
import eu.toolchain.ogt.TypeDecoder;
import eu.toolchain.ogt.TypeEncoder;

import java.util.List;

public interface EntityBinding {
    List<? extends FieldMapping> fields();

    <T> Object decodeEntity(
        EntityDecoder<T> entityDecoder, TypeDecoder<T> decoder, Context path
    );

    <T> T encodeEntity(
        EntityEncoder<T> entityEncoder, TypeEncoder<T> encoder, Context path, Object value
    );
}
