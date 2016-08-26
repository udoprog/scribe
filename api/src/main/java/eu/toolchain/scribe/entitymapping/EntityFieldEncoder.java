package eu.toolchain.scribe.entitymapping;

import eu.toolchain.scribe.Context;

public interface EntityFieldEncoder<Target, Source> {
  String getName();

  Target encode(Context path, Source instance);
}
