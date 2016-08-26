package eu.toolchain.scribe;

import eu.toolchain.scribe.annotations.EntityCreator;
import eu.toolchain.scribe.annotations.Property;
import eu.toolchain.scribe.entitymapping.BuilderEntityMapping;
import eu.toolchain.scribe.entitymapping.DefaultEntityMapping;
import eu.toolchain.scribe.typealias.AliasTypeMapping;
import eu.toolchain.scribe.typemapping.ConcreteEntityTypeMapping;
import eu.toolchain.scribe.typemapping.EntityTypeMapping;
import lombok.Data;
import lombok.experimental.Builder;
import org.junit.Test;

import java.beans.ConstructorProperties;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;

public class EntityMapperTest {
  private final EntityMapper mapper = EntityMapper.nativeBuilder().build();

  @Data
  @Builder
  public static class BuilderTest {
    private final String string;
  }

  @Test
  public void testBuilder() {
    final EntityTypeMapping a = (EntityTypeMapping) mapper.mapping(JavaType.of(BuilderTest.class));
    assertTrue(a instanceof ConcreteEntityTypeMapping);
    final ConcreteEntityTypeMapping ca = (ConcreteEntityTypeMapping) a;
    assertTrue(ca.getMapping() instanceof BuilderEntityMapping);
  }

  @Data
  @Builder
  public static class EntityCreatorTest {
    private final String string;

    @EntityCreator
    public EntityCreatorTest(@Property("string") final String string) {
      this.string = string;
    }
  }

  @Test
  public void testEntityCreator() {
    final EntityTypeMapping a =
        (EntityTypeMapping) mapper.mapping(JavaType.of(EntityCreatorTest.class));
    assertTrue(a instanceof ConcreteEntityTypeMapping);
    final ConcreteEntityTypeMapping ca = (ConcreteEntityTypeMapping) a;
    assertTrue(ca.getMapping() instanceof DefaultEntityMapping);
  }

  @Test
  public void testPrimitiveTypes() {
    Stream
        .of(boolean.class, short.class, int.class, long.class, float.class, double.class)
        .forEach(t -> {
          final JavaType type = JavaType.of(t);
          assertThat(mapper.mapping(type), instanceOf(EncodedTypeMapping.class));
          assertThat(mapper.mapping(type.asBoxed()), instanceOf(EncodedTypeMapping.class));
        });

    // aliases
    Stream.of(byte.class, char.class).forEach(t -> {
      final JavaType type = JavaType.of(t);
      assertThat(mapper.mapping(type), instanceOf(AliasTypeMapping.class));
      assertThat(mapper.mapping(type.asBoxed()), instanceOf(AliasTypeMapping.class));
    });
  }

  public static class ConstructorPropertiesVanilla {
    private final String name;

    @ConstructorProperties({"name"})
    public ConstructorPropertiesVanilla(final String name) {
      this.name = name;
    }

    public String getName() {
      return name;
    }
  }

  @Test
  public void testConstructorPropertiesVanilla() {
    assertThat(mapper.mapping(ConstructorPropertiesVanilla.class),
        instanceOf(ConcreteEntityTypeMapping.class));
  }

  public static class ConstructorPropertiesPropertyOverride {
    private final String name;

    @ConstructorProperties({"name"})
    public ConstructorPropertiesPropertyOverride(final @Property("other") String name) {
      this.name = name;
    }

    public String getOther() {
      return name;
    }
  }

  @Test
  public void testConstructorPropertiesPropertyOverride() {
    assertThat(mapper.mapping(ConstructorPropertiesPropertyOverride.class),
        instanceOf(ConcreteEntityTypeMapping.class));
  }
}
