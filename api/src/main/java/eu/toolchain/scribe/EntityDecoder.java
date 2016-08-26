package eu.toolchain.scribe;

/**
 * @param <Target> Target type of encoding.
 * @param <Source> Source type of encoding.
 */
public interface EntityDecoder<Target, Source> extends Decoder<Target, Source> {
  Decoded<Source> decode(EntityFieldsDecoder<Target> encoder, Context path);
}
