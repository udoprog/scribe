package eu.toolchain.scribe;

import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

@Data
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class Flags {
  static final Flags EMPTY_FLAGS = new Flags(Collections.emptySet());

  private final Set<Flag> flags;

  /**
   * Check if the current field has the given flag.
   *
   * @param flag Flag to check for.
   * @return {@code true} if the current field has the given flag.
   */
  public <F extends Flag> Stream<F> getFlag(Class<F> flag) {
    return getFlags().stream().filter(flag::isInstance).map(flag::cast);
  }

  public static Flags empty() {
    return EMPTY_FLAGS;
  }

  public static Flags copyOf(final Collection<Flag> flags) {
    return new Flags(new HashSet<>(flags));
  }
}
