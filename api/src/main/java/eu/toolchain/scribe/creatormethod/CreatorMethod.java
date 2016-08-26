package eu.toolchain.scribe.creatormethod;

import eu.toolchain.scribe.EntityField;

import java.util.List;
import java.util.Optional;

public interface CreatorMethod extends InstanceBuilder {
  /**
   * Get the list of fields detected by the creator method.
   *
   * @return A list of fields.
   */
  List<EntityField> getFields();

  /**
   * Field names, if they are known.
   *
   * @return An optional list of field names.
   */
  Optional<List<String>> getFieldNames();
}
