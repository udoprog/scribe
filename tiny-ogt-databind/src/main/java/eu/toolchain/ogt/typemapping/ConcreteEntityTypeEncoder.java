package eu.toolchain.ogt.typemapping;

import eu.toolchain.ogt.Context;
import eu.toolchain.ogt.EntityEncoder;
import eu.toolchain.ogt.EntityTypeEncoder;
import lombok.Data;

import java.util.Optional;

@Data
public class ConcreteEntityTypeEncoder<Target> implements EntityTypeEncoder<Target, Object> {
    private final Optional<String> typeName;
    private final EntityTypeEncoder<Target, Object> encoding;

    @Override
    public Target encode(
        final EntityEncoder<Target> decoder, final Context path, final Object instance
    ) {
        if (typeName.isPresent()) {
            decoder.setType(typeName.get());
        }

        return encoding.encode(decoder, path, instance);
    }
}
