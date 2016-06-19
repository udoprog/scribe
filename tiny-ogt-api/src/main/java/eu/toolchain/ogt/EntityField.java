package eu.toolchain.ogt;

import lombok.Data;

import java.lang.reflect.Type;
import java.util.Optional;

@Data
public class EntityField {
    private final int index;
    private final Annotations annotations;
    private final Optional<Type> type;
    private final Optional<String> name;

    /**
     * Return a new EntityField instance with the given name.
     *
     * @param name Name to associate with the new instance.
     * @return A new instance with the given name.
     */
    public EntityField withName(final String name) {
        return new EntityField(index, annotations, type, Optional.of(name));
    }
}
