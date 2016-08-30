package eu.toolchain.scribe;

import lombok.Data;

import java.util.Optional;

@Data
public class SubType {
  private final ClassMapping mapping;
  private final Optional<String> name;
}
