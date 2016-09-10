package eu.toolchain.scribe;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

public interface Matchers {
  static Matcher<ContextException> contextException(final String path) {
    return new TypeSafeMatcher<ContextException>() {
      @Override
      protected boolean matchesSafely(final ContextException item) {
        return path.equals(item.getPath().path());
      }

      @Override
      public void describeTo(final Description description) {
        description.appendText("contextException(" + path + ")");
      }
    };
  }
}
