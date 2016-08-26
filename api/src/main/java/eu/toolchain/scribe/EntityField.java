package eu.toolchain.scribe;

import lombok.Data;

import java.util.Optional;

@Data
public class EntityField {
  private final int index;
  private final Annotations annotations;
  private final JavaType type;
  private final Optional<String> name;
}
