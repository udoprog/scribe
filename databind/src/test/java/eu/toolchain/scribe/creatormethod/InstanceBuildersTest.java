package eu.toolchain.scribe.creatormethod;

import eu.toolchain.scribe.ConstructorInstanceBuilder;
import eu.toolchain.scribe.Scribe;
import eu.toolchain.scribe.EntityResolver;
import eu.toolchain.scribe.InstanceBuilder;
import eu.toolchain.scribe.StaticMethodInstanceBuilder;
import eu.toolchain.scribe.Mapping;
import eu.toolchain.scribe.annotations.EntityCreator;
import eu.toolchain.scribe.annotations.Property;
import eu.toolchain.scribe.reflection.JavaType;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

public class InstanceBuildersTest {
  private static final JavaType STRING = JavaType.of(String.class);

  private EntityResolver resolver;
  private Mapping string;

  @Before
  public void setup() {
    resolver = spy(Scribe.nativeBuilder().build());
    string = mock(Mapping.class);

    doReturn(string).when(resolver).mapping(STRING);
  }

  static class BadEntity {
    public BadEntity(final String ghost) {
    }
  }

  @Test
  public void testBadEntity() {
    Optional<InstanceBuilder> method = resolver.detectInstanceBuilder(JavaType.of(BadEntity.class));
    assertFalse(method.isPresent());
  }

  static class Constructor {
    @EntityCreator
    public Constructor(
        @Property("field") final String field, @Property("indexed") final String indexed
    ) {
    }
  }

  @Test
  public void testConstructor() {
    Optional<InstanceBuilder> method =
        resolver.detectInstanceBuilder(JavaType.of(Constructor.class));

    assertTrue(method.isPresent());
    final InstanceBuilder creator = method.get();
    assertTrue(creator instanceof ConstructorInstanceBuilder);
    final ConstructorInstanceBuilder c = (ConstructorInstanceBuilder) creator;

    checkFields(c);
  }

  static class StaticMethod {
    @EntityCreator
    public static StaticMethod build(
        @Property("field") final String field, @Property("indexed") final String indexed
    ) {
      return new StaticMethod();
    }
  }

  @Test
  public void testStaticMethod() {
    Optional<InstanceBuilder> method =
        resolver.detectInstanceBuilder(JavaType.of(StaticMethod.class));

    assertTrue(method.isPresent());
    final InstanceBuilder creator = method.get();
    assertTrue(creator instanceof StaticMethodInstanceBuilder);
    final StaticMethodInstanceBuilder c = (StaticMethodInstanceBuilder) creator;

    checkFields(c);
  }

  private void checkFields(final InstanceBuilder c) {
    assertEquals(2, c.getFields().size());

    assertEquals(STRING, c.getFields().get(0).getType());
    assertTrue(c.getFields().get(0).getAnnotations().isAnnotationPresent(Property.class));

    assertEquals(STRING, c.getFields().get(1).getType());
    assertTrue(c.getFields().get(1).getAnnotations().isAnnotationPresent(Property.class));
  }
}
