package eu.toolchain.ogt.binding;

import java.util.List;

import eu.toolchain.ogt.Context;
import eu.toolchain.ogt.EntityEncoder;
import eu.toolchain.ogt.FieldEncoder;

/**
 * A type binder mixin that implements a common
 * {@link #encodeEntity(EntityEncoder, Object, Context)} method that uses fields of type
 * {@link TypeFieldMapping}.
 *
 * @author udoprog
 */
public interface SetEntityTypeBinding extends Binding {
    List<? extends TypeFieldMapping> fields();

    @Override
    default Object encodeEntity(EntityEncoder encoder, FieldEncoder<?> fieldEncoder, Object entity,
            final Context path) {
        for (final TypeFieldMapping m : fields()) {
            final Object value;

            final Context p = path;

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
                    encoder.setField(m, p, v);
                } catch (Exception e) {
                    throw p.error("Failed to encode field", e);
                }
            });
        }

        return encoder.encode();
    }
}
