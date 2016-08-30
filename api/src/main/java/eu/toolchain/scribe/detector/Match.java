package eu.toolchain.scribe.detector;

import lombok.Data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
public class Match<T> {
  private final T value;
  private final MatchPriority priority;

  public static <T> Function<T, Match<T>> withPriority(final MatchPriority priority) {
    return value -> new Match<>(value, priority);
  }

  private static final Comparator<Match<?>> BY_SCORE_COMPARATOR =
      (a, b) -> Integer.compare(b.getPriority().ordinal(), a.getPriority().ordinal());

  public static <S, T> Optional<T> bestUniqueMatch(
      Stream<S> alternatives, Function<S, Stream<Match<T>>> map
  ) {
    final List<Match<T>> results = alternatives.flatMap(map).collect(Collectors.toList());

    final List<Match<T>> sorted = new ArrayList<>(results);
    Collections.sort(sorted, BY_SCORE_COMPARATOR);

    if (results.size() > 1) {
      if (sorted.get(0).getPriority() == sorted.get(1).getPriority()) {
        throw new IllegalArgumentException(
            "Found multiple matches with the same priority: " + sorted);
      }
    }

    return sorted.stream().map(Match::getValue).findFirst();
  }
}
