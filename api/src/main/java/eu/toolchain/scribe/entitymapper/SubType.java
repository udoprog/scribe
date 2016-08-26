package eu.toolchain.scribe.entitymapper;

import eu.toolchain.scribe.typemapping.EntityTypeMapping;
import lombok.Data;

import java.util.Optional;

@Data
public class SubType {
  private final EntityTypeMapping mapping;
  private final Optional<String> name;
}
