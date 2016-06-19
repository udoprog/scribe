package eu.toolchain.ogt;

/**
 * @param <Target> Target type of encoding.
 * @param <Source> Source type of encoding.
 */
public interface EntityTypeEncoder<Target, Source> {
    Target encode(EntityEncoder<Target> decoder, Context path, Source instance);
}
