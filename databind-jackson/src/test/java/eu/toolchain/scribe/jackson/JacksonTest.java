package eu.toolchain.scribe.jackson;

import eu.toolchain.scribe.AbstractDatabindTest;
import eu.toolchain.scribe.JacksonAnnotationsModule;
import eu.toolchain.scribe.NativeAnnotationsModule;
import eu.toolchain.scribe.Option;
import eu.toolchain.scribe.Scribe;
import eu.toolchain.scribe.StringEncoding;
import eu.toolchain.scribe.TypeReference;
import lombok.Data;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static eu.toolchain.scribe.Matchers.contextException;

public class JacksonTest extends AbstractDatabindTest {
  @Rule
  public ExpectedException exception = ExpectedException.none();

  private JacksonMapper mapper = new JacksonMapper(Scribe
      .defaultBuilder()
      .install(new JacksonAnnotationsModule())
      .install(new NativeAnnotationsModule())
      .build());

  @Override
  protected <S> StringEncoding<S> encodingFor(
      final TypeReference<S> type, final Option... options
  ) {
    return mapper.withOptions(options).stringEncodingFor(type);
  }

  @Override
  protected <S> StringEncoding<S> encodingFor(final Class<S> type, final Option... options) {
    return mapper.withOptions(options).stringEncodingFor(type);
  }

  @Data
  public static class DeepContextError {
    private final Child1 field;

    @Data
    public static class Child1 {
      private final Child2 field;
    }

    @Data
    public static class Child2 {
      public Child2() {
        throw new RuntimeException("oops");
      }
    }
  }

  @Test
  public void testDeepContextError() {
    exception.expect(contextException("field.field"));
    final StringEncoding<DeepContextError> encoding = encodingFor(DeepContextError.class);
    encoding.decode("{\"field\": {\"field\": {}}}");
  }
}
