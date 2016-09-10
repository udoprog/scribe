package eu.toolchain.scribe;

/**
 * @param <Target> Target type of encoding.
 * @param <Source> Source type of encoding.
 */
public interface EntityDecoder<Target, EntityTarget, Source> extends Decoder<Target, Source> {
  Source decodeEntity(Context path, EntityTarget entity, EntityFieldsDecoder<Target> decoder);

  Source decodeEntity(Context path, EntityTarget entity);
}
