package eu.toolchain.scribe;

import lombok.Data;

import java.util.function.Function;

@Data
public class Match<T> {
  private final T value;
  private final MatchPriority priority;

  public static <T> Function<T, Match<T>> withPriority(final MatchPriority priority) {
    return value -> new Match<>(value, priority);
  }
}
