package eu.toolchain.scribe;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class CaseFormatTest {
  @Test
  public void testBasic() {
    assertThat(CaseFormat.lowerCamelToUpperCamel("helloWorld"), is("HelloWorld"));
    assertThat(CaseFormat.lowerCamelToUpperCamel("HelloWorld"), is("HelloWorld"));
  }
}
