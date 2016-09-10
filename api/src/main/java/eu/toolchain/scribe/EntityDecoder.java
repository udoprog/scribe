package eu.toolchain.scribe;

/**
 * @param <Target> Target type of encoding.
 * @param <Source> Source type of encoding.
 */
public interface EntityDecoder<Target, EntityTarget, Source> extends Decoder<Target, Source> {
  Source decode(EntityFieldsDecoder<Target> encoder, Context path);

  Source decodeEntity(Context path, EntityTarget entity);
}
