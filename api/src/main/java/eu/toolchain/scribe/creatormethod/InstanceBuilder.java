package eu.toolchain.scribe.creatormethod;

import java.util.List;

public interface InstanceBuilder {
  /**
   * Build a new instance using the detected creator method.
   *
   * @param arguments Arguments to build new instance from.
   * @return The new instance.
   */
  Object newInstance(List<Object> arguments);
}
