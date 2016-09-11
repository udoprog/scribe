package eu.toolchain.scribe;

import eu.toolchain.scribe.reflection.Annotations;
import eu.toolchain.scribe.reflection.JavaType;
import lombok.Data;

@Data
public class EntityField {
  private final JavaType type;
  private final Annotations annotations;
  private final String serializedName;
  private final String fieldName;
}
