package eu.toolchain.scribe.automatter;

import eu.toolchain.scribe.Scribe;
import eu.toolchain.scribe.StringEncoding;
import eu.toolchain.scribe.jackson.JacksonMapper;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class AutoMatterTest {
  private final Scribe m = Scribe.defaultBuilder().install(new AutoMatterModule()).build();

  private final JacksonMapper mapper = new JacksonMapper(m);

  @Test
  public void testBasic() {
    final StringEncoding<Basic> encoding = mapper.stringEncodingFor(Basic.class);

    final Basic basic = new BasicBuilder().field("value").build();
    final String encoded = "{\"field\":\"value\"}";

    assertThat(encoding.encode(basic), is(encoded));
    assertThat(encoding.decode(encoded), is(basic));
  }
}
