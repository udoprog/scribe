package eu.toolchain.scribe.guava;

import com.google.common.base.Optional;
import eu.toolchain.scribe.ConcreteClassMapping;
import eu.toolchain.scribe.DefaultEntityFieldMapping;
import eu.toolchain.scribe.Scribe;
import eu.toolchain.scribe.MethodClassEncoding;
import eu.toolchain.scribe.OptionalMapping;
import eu.toolchain.scribe.reflection.JavaType;
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
    final ConcreteClassMapping mapping =
        (ConcreteClassMapping) m.mapping(JavaType.of(GuavaOptional.class));

    final MethodClassEncoding encoding = (MethodClassEncoding) mapping.getDeferred();
    final DefaultEntityFieldMapping field = encoding.getFields().get(0);

    assertThat(field.getMapping(), instanceOf(OptionalMapping.class));
  }
}
