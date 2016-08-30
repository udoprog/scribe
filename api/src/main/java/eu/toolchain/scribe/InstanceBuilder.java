package eu.toolchain.scribe;

import java.util.List;
import java.util.Optional;

/**
 * Describes a way that an instance of a type can be created and the fields that it has.
 */
public interface InstanceBuilder {
  /**
   * Build a new instance using the detected creator method.
   *
   * @param arguments Arguments to build new instance from.
   * @return The new instance.
   */
  Object newInstance(List<Object> arguments);

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
