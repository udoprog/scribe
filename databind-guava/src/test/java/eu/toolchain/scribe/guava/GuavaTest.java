package eu.toolchain.scribe.guava;

import com.google.common.base.Optional;
import eu.toolchain.scribe.EntityMapper;
import eu.toolchain.scribe.JavaType;
import eu.toolchain.scribe.entitymapping.DefaultEntityFieldMapping;
import eu.toolchain.scribe.typemapping.ConcreteEntityTypeMapping;
import eu.toolchain.scribe.typemapping.OptionalTypeMapping;
import lombok.Data;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;

public class GuavaTest {
  private EntityMapper m = EntityMapper.defaultBuilder().register(new GuavaModule()).build();

  @Data
  static class GuavaOptional {
    private final Optional<String> field;
  }

  @Test
  public void testGuavaOptional() {
    final ConcreteEntityTypeMapping mapping =
        (ConcreteEntityTypeMapping) m.mapping(JavaType.of(GuavaOptional.class));

    final DefaultEntityFieldMapping field =
        (DefaultEntityFieldMapping) mapping.getMapping().fields().get(0);

    assertThat(field.getMapping(), instanceOf(OptionalTypeMapping.class));
  }
}
