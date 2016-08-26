package eu.toolchain.scribe;

import java.util.function.Consumer;

/**
 * @param <Target> Target type of encoding.
 * @param <Source> Source type of encoding.
 */
public interface StreamEncoder<Target, Source> {
  void streamEncode(Context path, Source instance, Target target);

  void streamEncodeEmpty(Context path, Target target);

  default void streamEncode(Source instance, Target target) {
    streamEncode(Context.ROOT, instance, target);
  }

  default void streamEncodeOptionally(
      Context path, Source instance, Target target, Consumer<Runnable> callback
  ) {
    callback.accept(() -> streamEncode(path, instance, target));
  }
}
