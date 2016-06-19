package eu.toolchain.ogt;

import java.util.Optional;

/**
 * @param <Target> Target type of encoding.
 * @param <Source> Source type of encoding.
 */
public interface Decoder<Target, Source> {
    Source decode(Context path, Target instance);

    default Optional<?> fromOptional(Optional<?> value) {
        return value;
    }
}
