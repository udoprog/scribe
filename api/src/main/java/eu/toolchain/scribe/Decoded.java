package eu.toolchain.scribe;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Encapsulate a decoded value. <p> Many target encodings contains the concept of the absence of a
 * value, like <code>null</code> for JSON. </p> This type encapsulates the case where a value has
 * been decoded, but should be considered absent. It behaves very similarly to {@link
 * java.util.Optional}. It was made distinct to avoid confusion with other classes that uses both
 * and add the ability to introduce more useful utility methods specifically related to decoding
 * values.
 *
 * @param <T> The encapsulated type.
 */
public interface Decoded<T> {
  /**
   * Map the decoded value from one type to another value.
   */
  <O> Decoded<O> map(Function<? super T, ? extends O> function);

  /**
   * Map the decoded value from one type to another decoded value.
   */
  <O> Decoded<O> flatMap(Function<? super T, Decoded<O>> function);

  /**
   * Return the decoded value, or throw an exception if absent.
   */
  <X extends Throwable> T orElseThrow(Supplier<? extends X> supplier) throws X;

  /**
   * Handle the decoded value both if present or absent.
   * <p>
   * This method will always return a decoded value that is present.
   */
  <O> Decoded<O> handle(
      Function<? super T, ? extends O> present, Supplier<? extends O> absent
  );

  /**
   * Handle the decoded value if absent.
   * <p>
   * This method will always return a decoded value that is present.
   */
  Decoded<T> handleAbsent(Supplier<? extends T> supplier);

  /**
   * Handle the decoded value if present.
   */
  void ifPresent(Consumer<? super T> consumer);

  /**
   * Singleton absent instance.
   */
  Decoded<?> ABSENT = new Absent<>();

  /**
   * Get a decoded value that is absent.
   */
  @SuppressWarnings("unchecked")
  static <T> Decoded<T> absent() {
    return (Decoded<T>) ABSENT;
  }

  /**
   * Make a decoded value that is present, and contains the given value.
   */
  static <T> Decoded<T> of(T value) {
    return new Present<>(value);
  }

  /**
   * Make a decoded value that is present and contains the given value that may be {@code null} to
   * represent absence.
   */
  static <T> Decoded<T> ofNullable(T value) {
    if (value == null) {
      return absent();
    }

    return new Present<>(value);
  }

  /**
   * Implementation of {@link Decoded} for values which are present.
   */
  @RequiredArgsConstructor(access = AccessLevel.PACKAGE)
  @EqualsAndHashCode
  class Present<T> implements Decoded<T> {
    private final T value;

    @Override
    public <O> Decoded<O> map(final Function<? super T, ? extends O> function) {
      return new Present<>(function.apply(value));
    }

    @Override
    public <O> Decoded<O> flatMap(
        final Function<? super T, Decoded<O>> function
    ) {
      return function.apply(value);
    }

    @Override
    public <X extends Throwable> T orElseThrow(final Supplier<? extends X> supplier) throws X {
      return value;
    }

    @Override
    public <O> Decoded<O> handle(
        final Function<? super T, ? extends O> present, final Supplier<? extends O> absent
    ) {
      return new Present<>(present.apply(value));
    }

    @Override
    public void ifPresent(final Consumer<? super T> consumer) {
      consumer.accept(value);
    }

    @Override
    public Decoded<T> handleAbsent(final Supplier<? extends T> supplier) {
      return this;
    }

    @Override
    public String toString() {
      return "Decoded[" + value + "]";
    }
  }

  /**
   * Implementation of {@link Decoded} for values which are absent.
   */
  @RequiredArgsConstructor(access = AccessLevel.PACKAGE)
  @EqualsAndHashCode
  class Absent<T> implements Decoded<T> {
    @Override
    public <O> Decoded<O> map(final Function<? super T, ? extends O> function) {
      return absent();
    }

    @Override
    public <O> Decoded<O> flatMap(
        final Function<? super T, Decoded<O>> function
    ) {
      return absent();
    }

    @Override
    public <X extends Throwable> T orElseThrow(final Supplier<? extends X> supplier) throws X {
      throw supplier.get();
    }

    @Override
    public <O> Decoded<O> handle(
        final Function<? super T, ? extends O> present, final Supplier<? extends O> absent
    ) {
      return new Present<>(absent.get());
    }

    @Override
    public void ifPresent(final Consumer<? super T> consumer) {
    }

    @Override
    public Decoded<T> handleAbsent(final Supplier<? extends T> supplier) {
      return new Present<>(supplier.get());
    }

    @Override
    public String toString() {
      return "Decoded[]";
    }
  }
}
