package eu.toolchain.scribe;

import lombok.Data;

public interface DatabindOptions {
  OptionalEmptyAsNull OPTIONAL_EMPTY_AS_NULL = new OptionalEmptyAsNull();

  /**
   * Option to indicate that optional's empty state should be encoded as null.
   * <p>
   * The default behaviour is that the value is omitted for the cases where possible.
   */
  @Data
  class OptionalEmptyAsNull implements Option {
  }

  @Data
  class TypeFieldName implements Option {
    private final String name;
  }
}
