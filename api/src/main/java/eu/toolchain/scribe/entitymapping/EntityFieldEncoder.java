package eu.toolchain.scribe.entitymapping;

import eu.toolchain.scribe.Context;

import java.util.function.Consumer;

public interface EntityFieldEncoder<Target, Source> {
  String getName();

  Target encode(Context path, Source instance);

  void encodeOptionally(Context path, Source instance, Consumer<Target> consumer);
}
