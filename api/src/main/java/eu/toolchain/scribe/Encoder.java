package eu.toolchain.scribe;

import java.util.function.Consumer;

/**
 * @param <Target> Target type of encoder.
 * @param <Source> Source type of encoder.
 */
public interface Encoder<Target, Source> {
  /**
   * Encode the given instance to the target type.
   *
   * @param path Current encoder path.
   * @param instance Instance to encode.
   * @return Target instance.
   */
  Target encode(Context path, Source instance);

  /**
   * Encode the given instance to the target type.
   *
   * @param instance Instance to encode.
   * @return Target instance.
   */
  default Target encode(Source instance) {
    return encode(Context.ROOT, instance);
  }

  /**
   * Encode the special empty value to the target type.
   *
   * @param path Current encoder path.
   * @return Target instance.
   */
  Target encodeEmpty(Context path);

  /**
   * Encode the given instance to the target type optionally.
   *
   * @param path Current encoder path.
   * @param instance Instance to encode.
   * @param callback Callback that will be called with the current target instance of the value has
   * been encoded.
   */
  default void encodeOptionally(Context path, Source instance, Consumer<Target> callback) {
    callback.accept(encode(path, instance));
  }
}
