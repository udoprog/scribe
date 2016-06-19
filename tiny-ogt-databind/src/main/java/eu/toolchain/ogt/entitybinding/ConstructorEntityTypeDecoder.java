package eu.toolchain.ogt.entitybinding;

import eu.toolchain.ogt.Context;
import eu.toolchain.ogt.EntityDecoder;
import eu.toolchain.ogt.EntityTypeDecoder;
import eu.toolchain.ogt.creatormethod.InstanceBuilder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ConstructorEntityTypeDecoder<Target> implements EntityTypeDecoder<Target, Object> {
    private final List<EntityFieldDecoder<Target, Object>> fields;
    private final InstanceBuilder instanceBuilder;

    @Override
    public Object decode(
        final EntityDecoder<Target> encoder, final Context path
    ) {
        final List<Object> arguments = new ArrayList<>();

        for (final EntityFieldDecoder<Target, Object> m : fields) {
            final Context p = path.push(m.getName());

            final Object value = m
                .fromOptional(encoder.decodeField(m, p))
                .orElseThrow(() -> p.error("Missing required field (" + m.getName() + ")"));

            arguments.add(value);
        }

        try {
            return instanceBuilder.newInstance(arguments);
        } catch (final Exception e) {
            throw path.error("Could not build instance using " + instanceBuilder, e);
        }
    }
}
