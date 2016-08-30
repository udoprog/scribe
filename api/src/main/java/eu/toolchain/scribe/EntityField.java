package eu.toolchain.scribe;

import eu.toolchain.scribe.reflection.Annotations;
import eu.toolchain.scribe.reflection.JavaType;
import lombok.Data;

import java.util.Optional;

@Data
public class EntityField {
  /**
   * If this field is immediate or not.
   * <p>
   * Immediate fields are declared immediately on the class.
   */
  private final boolean immediate;

  private final int index;
  private final Annotations annotations;
  private final JavaType type;
  private final Optional<String> name;
}
