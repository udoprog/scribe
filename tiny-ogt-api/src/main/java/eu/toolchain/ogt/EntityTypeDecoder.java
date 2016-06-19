package eu.toolchain.ogt;

/**
 * @param <Target> Target type of encoding.
 * @param <Source> Source type of encoding.
 */
public interface EntityTypeDecoder<Target, Source> {
    Source decode(EntityDecoder<Target> encoder, Context path);
}
