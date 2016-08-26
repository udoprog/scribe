package eu.toolchain.scribe.entitymapping;

import eu.toolchain.scribe.Context;

import java.util.function.Consumer;

public interface EntityFieldStreamEncoder<Target, Source> {
  String getName();

  void streamEncode(Context path, Source instance, Target target);

  void streamEncodeOptionally(
      Context path, Source instance, Target target, Consumer<Runnable> callback
  );
}
