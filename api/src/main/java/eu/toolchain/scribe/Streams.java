package eu.toolchain.scribe;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface Streams {
  static <T> T streamRequireOne(final Stream<T> stream) {
    return streamRequireOne(stream, values -> {
      if (values.isEmpty()) {
        return new IllegalArgumentException("Stream is empty");
      }

      return new IllegalArgumentException("Stream returned more than one result (" + values + ")");
    });
  }

  static <T, E extends RuntimeException> T streamRequireOne(
      final Stream<T> stream, final Function<List<T>, E> error
  ) {
    final List<T> values = stream.collect(Collectors.toList());

    if (values.size() == 1) {
      return values.get(0);
    }

    throw error.apply(values);
  }
}
