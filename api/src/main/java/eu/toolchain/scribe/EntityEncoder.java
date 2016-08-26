package eu.toolchain.scribe;

/**
 * @param <Target> Target type of encoding.
 * @param <Source> Source type of encoding.
 */
public interface EntityEncoder<Target, Source> extends Encoder<Target, Source> {
  Runnable EMPTY_CALLBACK = () -> {
  };

  Target encode(
      EntityFieldsEncoder<Target> decoder, Context path, Source instance, Runnable callback
  );
}
