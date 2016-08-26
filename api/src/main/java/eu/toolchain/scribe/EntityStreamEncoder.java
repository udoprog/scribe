package eu.toolchain.scribe;

/**
 * @param <Target> Target type of encoding.
 * @param <Source> Source type of encoding.
 */
public interface EntityStreamEncoder<Target, Source> extends StreamEncoder<Target, Source> {
  Runnable EMPTY_CALLBACK = () -> {
  };

  void streamEncode(
      EntityFieldsStreamEncoder<Target> encoder, Context path, Source instance, Target target,
      Runnable callback
  );
}
