package eu.toolchain.ogt.binding;

import eu.toolchain.ogt.Context;
import eu.toolchain.ogt.EntityEncoder;
import eu.toolchain.ogt.TypeEncoder;

public interface ReadFieldsEntityBinding extends EntityBinding {
    @Override
    default <T> T encodeEntity(
        EntityEncoder<T> entityEncoder, TypeEncoder<T> encoder, final Context path, Object entity
    ) {
        for (final FieldMapping m : fields()) {
            final Context p = path.push(m.name());

            final Object value;

            try {
                value = m.reader().read(entity);
            } catch (final Exception e) {
                throw p.error("Failed to read value using " + m.reader(), e);
            }

            if (value == null) {
                throw p.error("Null value read from " + m.reader());
            }

            m.type().asOptional(value).ifPresent(v -> {
                try {
                    entityEncoder.setField(m, p, v);
                } catch (Exception e) {
                    throw p.error("Failed to encode field", e);
                }
            });
        }

        return entityEncoder.build();
    }
}
