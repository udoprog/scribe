package eu.toolchain.scribe;

import lombok.Data;

import java.util.Optional;

@Data
public class SubType<Source> {
  private final ClassMapping<Source> mapping;
  private final Optional<String> name;
}
