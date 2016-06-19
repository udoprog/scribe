package eu.toolchain.ogt;

import lombok.Data;

import java.util.function.Function;

@Data
public class Match<T> {
    private final T value;
    private final Priority priority;

    public static <T> Function<T, Match<T>> withPriority(final Priority priority) {
        return value -> new Match<>(value, priority);
    }
}
