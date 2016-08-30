package eu.toolchain.scribe.guava;

import com.google.common.base.Optional;
import eu.toolchain.scribe.EntityMapperBuilder;
import eu.toolchain.scribe.Module;
import eu.toolchain.scribe.OptionalMapping;

public class GuavaModule implements Module {
  @Override
  public void register(final EntityMapperBuilder builder) {
    builder.mapping(
        OptionalMapping.forType(Optional.class, Optional::isPresent, Optional::get,
            Optional::of, Optional::absent));
  }
}
