package eu.toolchain.ogt.binding;

import eu.toolchain.ogt.Context;
import eu.toolchain.ogt.EntityEncoder;
import eu.toolchain.ogt.TypeEncoder;

import java.util.List;

/**
 * A type binder mixin that implements a common {@link #encodeEntity(eu.toolchain.ogt.EntityEncoder,
 * eu.toolchain.ogt.TypeEncoder, Object, eu.toolchain.ogt.Context)} method that uses fields of type
 * {@link TypeFieldMapping}.
 *
 * @author udoprog
 */
public interface SetEntityTypeBinding<T> extends Binding<T> {
    List<? extends TypeFieldMapping> fields();

    @Override
    default T encodeEntity(
        EntityEncoder<T> entityEncoder, TypeEncoder<T> encoder, final Context path, Object entity
    ) {
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
                    entityEncoder.setField(m, p, v);
                } catch (Exception e) {
                    throw p.error("Failed to encode field", e);
                }
            });
        }

        return entityEncoder.build();
    }
}
