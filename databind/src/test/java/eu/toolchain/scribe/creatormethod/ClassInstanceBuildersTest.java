package eu.toolchain.scribe.creatormethod;

import eu.toolchain.scribe.ConstructorClassInstanceBuilder;
import eu.toolchain.scribe.EntityResolver;
import eu.toolchain.scribe.ClassInstanceBuilder;
import eu.toolchain.scribe.Mapping;
import eu.toolchain.scribe.Scribe;
import eu.toolchain.scribe.StaticMethodClassInstanceBuilder;
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

public class ClassInstanceBuildersTest {
  private static final JavaType STRING = JavaType.of(String.class);

  private EntityResolver resolver;
  private Mapping<Object> string;

  @SuppressWarnings("unchecked")
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
    Optional<ClassInstanceBuilder<Object>> method =
        resolver.detectInstanceBuilder(JavaType.of(BadEntity.class));
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
    Optional<ClassInstanceBuilder<Object>> method =
        resolver.detectInstanceBuilder(JavaType.of(Constructor.class));

    assertTrue(method.isPresent());
    final ClassInstanceBuilder<Object> creator = method.get();
    assertTrue(creator instanceof ConstructorClassInstanceBuilder);
    final ConstructorClassInstanceBuilder<Object> c = (ConstructorClassInstanceBuilder<Object>) creator;

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
    Optional<ClassInstanceBuilder<Object>> method =
        resolver.detectInstanceBuilder(JavaType.of(StaticMethod.class));

    assertTrue(method.isPresent());
    final ClassInstanceBuilder<Object> creator = method.get();
    assertTrue(creator instanceof StaticMethodClassInstanceBuilder);
    final StaticMethodClassInstanceBuilder<Object> c = (StaticMethodClassInstanceBuilder<Object>) creator;

    checkFields(c);
  }

  private void checkFields(final ClassInstanceBuilder<Object> c) {
    assertEquals(2, c.getFields().size());

    assertEquals(STRING, c.getFields().get(0).getType());
    assertTrue(c.getFields().get(0).getAnnotations().isAnnotationPresent(Property.class));

    assertEquals(STRING, c.getFields().get(1).getType());
    assertTrue(c.getFields().get(1).getAnnotations().isAnnotationPresent(Property.class));
  }
}
