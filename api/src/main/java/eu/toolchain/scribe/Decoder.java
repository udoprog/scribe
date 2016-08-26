package eu.toolchain.scribe;

/**
 * @param <Target> Target type of encoding.
 * @param <Source> Source type of encoding.
 */
@FunctionalInterface
public interface Decoder<Target, Source> {
  /**
   * Decode the given instance to the source type.
   *
   * @param path Current path of the decoder.
   * @param instance Target instance to decode.
   * @return Source instance if successful, absent otherwise.
   */
  Decoded<Source> decode(Context path, Target instance);

  /**
   * Decode the given instance to the source type.
   * <p>
   * This method uses the context root as a default path.
   *
   * @param instance Target instance to decode.
   * @return Source instance if successful, absent otherwise.
   */
  default Decoded<Source> decode(Target instance) {
    return decode(Context.ROOT, instance);
  }

  /**
   * Decode the given instance to the source type optionally.
   *
   * @param path Current path of the decoder.
   * @param instance Decoded target instance which may be absent.
   * @return Source instance if successful, absent otherwise.
   */
  default Decoded<Source> decodeOptionally(
      final Context path, final Decoded<Target> instance
  ) {
    return instance.flatMap(i -> decode(path, i));
  }
}
