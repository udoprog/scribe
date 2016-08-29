package eu.toolchain.scribe;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

public interface Matchers {
  static Matcher<MappingException> mappingException(final String path) {
    return new TypeSafeMatcher<MappingException>() {
      @Override
      protected boolean matchesSafely(final MappingException item) {
        return path.equals(item.getPath().path());
      }

      @Override
      public void describeTo(final Description description) {
        description.appendText("mappingException(" + path + ")");
      }
    };
  }
}
