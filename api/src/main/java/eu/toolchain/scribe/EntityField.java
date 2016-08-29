package eu.toolchain.scribe;

import lombok.Data;

import java.util.Optional;

@Data
public class EntityField {
  /**
   * If this field is immediate or not.
   *
   * Immediate fields are declared immediately on the class.
   */
  private final boolean immediate;

  private final int index;
  private final Annotations annotations;
  private final JavaType type;
  private final Optional<String> name;
}
