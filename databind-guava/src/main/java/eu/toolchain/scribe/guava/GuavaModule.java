package eu.toolchain.scribe.guava;

import com.google.common.base.Optional;
import eu.toolchain.scribe.EntityMapperBuilder;
import eu.toolchain.scribe.Module;
import eu.toolchain.scribe.typemapping.OptionalTypeMapping;

public class GuavaModule implements Module {
  @Override
  public <T> EntityMapperBuilder<T> register(
      final EntityMapperBuilder<T> builder
  ) {
    builder.typeMapper(
        OptionalTypeMapping.forType(Optional.class, Optional::isPresent, Optional::get,
            Optional::of, Optional::absent));

    return builder;
  }
}
