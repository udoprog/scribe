package eu.toolchain.ogt.entitybinding;

import eu.toolchain.ogt.Context;
import eu.toolchain.ogt.EntityEncoder;
import eu.toolchain.ogt.EntityTypeEncoder;
import lombok.Data;

import java.util.List;

@Data
public class ReadFieldsEntityTypeEncoder<Target> implements EntityTypeEncoder<Target, Object> {
    private final List<? extends EntityFieldEncoder<Target, Object>> fields;

    @Override
    public Target encode(
        final EntityEncoder<Target> encoder, final Context path, final Object instance
    ) {
        for (final EntityFieldEncoder<Target, Object> m : fields) {
            final Context p = path.push(m.getName());

            final Object value;

            try {
                value = m.getReader().read(instance);
            } catch (final Exception e) {
                throw p.error("Failed to read value using " + m.getReader(), e);
            }

            if (value == null) {
                throw p.error("Null value read from " + m.getReader());
            }

            m.asOptional(value).ifPresent(v -> {
                try {
                    encoder.setField(m, p, v);
                } catch (Exception e) {
                    throw p.error("Failed to encode field", e);
                }
            });
        }

        return encoder.build();
    }
}
