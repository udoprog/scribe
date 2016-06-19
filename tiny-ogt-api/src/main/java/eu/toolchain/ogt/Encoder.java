package eu.toolchain.ogt;

import java.util.Optional;

/**
 * @param <Target> Target type of encoding.
 * @param <Source> Source type of encoding.
 */
public interface Encoder<Target, Source> {
    Target encode(Context path, Source instance);

    default Optional<Source> asOptional(Source object) {
        return Optional.of(object);
    }
}
