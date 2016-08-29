package eu.toolchain.scribe.entitymapping;

import eu.toolchain.scribe.Flags;

public interface EntityFieldDescriptor {
  /**
   * Get all flags associated with the given field.
   */
  Flags getFlags();
}
