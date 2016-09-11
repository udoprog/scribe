package eu.toolchain.scribe.guava;

import com.google.common.base.Optional;
import eu.toolchain.scribe.DatabindClassMapping;
import eu.toolchain.scribe.DefaultEntityFieldMapping;
import eu.toolchain.scribe.MethodClassEncoding;
import eu.toolchain.scribe.OptionalMapping;
import eu.toolchain.scribe.Scribe;
import lombok.Data;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;

public class GuavaTest {
  private Scribe m = Scribe.defaultBuilder().install(new GuavaModule()).build();

  @Data
  static class GuavaOptional {
    private final Optional<String> field;
  }

  @Test
  public void testGuavaOptional() {
    final DatabindClassMapping<GuavaOptional> mapping =
        (DatabindClassMapping<GuavaOptional>) m.mapping(GuavaOptional.class);

    final MethodClassEncoding<GuavaOptional> encoding =
        (MethodClassEncoding<GuavaOptional>) mapping.getDeferred();
    final DefaultEntityFieldMapping<Object> field = encoding.getFields().get(0);

    assertThat(field.getMapping(), instanceOf(OptionalMapping.class));
  }
}
