package eu.toolchain.scribe;

import java.util.List;

/**
 * Describes a way that an instance of a type can be created and the fields that it has.
 */
public interface ClassInstanceBuilder<Source> {
  InstanceBuilder<Source> getInstanceBuilder();

  /**
   * Get the list of fields detected by the creator method.
   *
   * @return A list of fields.
   */
  List<EntityField> getFields();
}
