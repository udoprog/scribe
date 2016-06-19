package eu.toolchain.ogt.entitybinding;

import eu.toolchain.ogt.Context;
import eu.toolchain.ogt.EntityDecoder;
import eu.toolchain.ogt.EntityTypeDecoder;
import lombok.Data;

import java.lang.reflect.Method;
import java.util.List;

@Data
public class BuilderEntityTypeDecoder<Target> implements EntityTypeDecoder<Target, Object> {
    private final List<BuilderEntityFieldDecoder<Target>> fields;
    private final Method newInstance;
    private final Method build;

    @Override
    public Object decode(
        final EntityDecoder<Target> encoder, final Context path
    ) {
        final Object builder;

        try {
            builder = newInstance.invoke(null);
        } catch (final ReflectiveOperationException e) {
            throw new RuntimeException("Failed to create instance of builder (" + newInstance + ")",
                e);
        }

        for (final BuilderEntityFieldDecoder<Target> m : fields) {
            final Context p = path.push(m.getName());

            final Object argument = m
                .fromOptional(encoder.decodeField(m, p))
                .orElseThrow(() -> p.error("Missing required field: " + m.getName()));

            if (argument == null) {
                throw new NullPointerException(m.getName());
            }

            try {
                m.setter().invoke(builder, argument);
            } catch (final Exception e) {
                throw p.error(
                    "Failed to invoke builder method " + m.getName() + " with argument (" +
                        argument +
                        ")", e);
            }
        }

        try {
            return build.invoke(builder);
        } catch (final Exception e) {
            throw new RuntimeException("Could not build instance using " + build, e);
        }
    }
}
