package eu.toolchain.scribe;

/**
 * @param <Target> Target type of encoding.
 * @param <Source> Source type of encoding.
 */
public interface EntityEncoder<Target, EntityTarget, Source> extends Encoder<Target, Source> {
  Runnable EMPTY_CALLBACK = () -> {
  };

  EntityTarget encode(
      EntityFieldsEncoder<Target, EntityTarget> decoder, Context path, Source instance,
      Runnable callback
  );

  EntityTarget encodeEntity(Context path, Object instance);
}
