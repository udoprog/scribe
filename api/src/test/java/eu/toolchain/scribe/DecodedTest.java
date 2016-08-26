package eu.toolchain.scribe;

import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static eu.toolchain.scribe.Decoded.absent;
import static eu.toolchain.scribe.Decoded.of;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class DecodedTest {
  private final int value = 0;
  private final int other = 100;

  private Decoded<Integer> present;
  private Decoded<Integer> absent;

  @Before
  public void setup() {
    present = of(value);
    absent = absent();
  }

  @Test
  public void testMap() {
    assertThat(present.map(o -> o + 1), is(of(value + 1)));
    assertThat(absent.map(o -> o + 1), is(absent()));
  }

  @Test
  public void testFlatMap() {
    assertThat(present.flatMap(o -> of(o + 1)), is(of(value + 1)));
    assertThat(present.flatMap(o -> absent()), is(absent()));
    assertThat(absent.flatMap(o -> of(o + 1)), is(absent()));
    assertThat(absent.flatMap(o -> absent()), is(absent()));
  }

  @Test
  public void testOrElseThrowPresent() {
    assertThat(present.orElseThrow(RuntimeException::new), is(value));
  }

  @Test(expected = RuntimeException.class)
  public void testOrElseThrowAbsent() {
    absent.orElseThrow(RuntimeException::new);
  }

  @Test
  public void testHandle() {
    assertThat(present.handle(v -> v + 1, () -> other), is(of(value + 1)));
    assertThat(absent.handle(v -> v + 1, () -> other), is(of(other)));
  }

  @Test
  public void testHandleAbsent() {
    assertThat(present.handleAbsent(() -> other), is(of(value)));
    assertThat(absent.handleAbsent(() -> other), is(of(other)));
  }

  @Test
  public void testIfPresent() {
    final AtomicInteger ticker = new AtomicInteger();

    assertThat(ticker.get(), is(0));

    absent.ifPresent(ticker::addAndGet);
    assertThat(ticker.get(), is(0));

    present.ifPresent(ticker::addAndGet);
    assertThat(ticker.get(), is(value));
  }
}
