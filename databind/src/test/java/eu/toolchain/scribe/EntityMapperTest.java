package eu.toolchain.scribe;

import eu.toolchain.scribe.annotations.EntityCreator;
import eu.toolchain.scribe.annotations.Property;
import eu.toolchain.scribe.reflection.JavaType;
import lombok.Data;
import lombok.experimental.Builder;
import org.junit.Test;

import java.beans.ConstructorProperties;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;

public class EntityMapperTest {
  private final EntityMapper mapper = EntityMapper.nativeBuilder().build();

  @Data
  @Builder
  public static class BuilderTest {
    private final String string;
  }

  @Test
  public void testBuilder() {
    final ClassMapping a = (ClassMapping) mapper.mapping(JavaType.of(BuilderTest.class));
    assertThat(a, instanceOf(ConcreteClassMapping.class));
    final ConcreteClassMapping ca = (ConcreteClassMapping) a;
    assertThat(ca.getDeferred(), instanceOf(BuilderClassEncoding.class));
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
    final ClassMapping a = (ClassMapping) mapper.mapping(JavaType.of(EntityCreatorTest.class));
    assertThat(a, instanceOf(ConcreteClassMapping.class));
    final ConcreteClassMapping ca = (ConcreteClassMapping) a;
    assertThat(ca.getDeferred(), instanceOf(MethodClassEncoding.class));
  }

  @Test
  public void testPrimitiveTypes() {
    Stream
        .of(boolean.class, short.class, int.class, long.class, float.class, double.class)
        .forEach(t -> {
          final JavaType type = JavaType.of(t);
          assertThat(mapper.mapping(type), instanceOf(EncodedMapping.class));
          assertThat(mapper.mapping(type.asBoxed()), instanceOf(EncodedMapping.class));
        });

    // aliases
    Stream.of(byte.class, char.class).forEach(t -> {
      final JavaType type = JavaType.of(t);
      assertThat(mapper.mapping(type), instanceOf(TypeAliasMapping.class));
      assertThat(mapper.mapping(type.asBoxed()), instanceOf(TypeAliasMapping.class));
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
        instanceOf(ConcreteClassMapping.class));
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
        instanceOf(ConcreteClassMapping.class));
  }
}
