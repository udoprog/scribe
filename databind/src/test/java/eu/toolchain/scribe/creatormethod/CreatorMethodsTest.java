package eu.toolchain.scribe.creatormethod;

import eu.toolchain.scribe.EntityMapper;
import eu.toolchain.scribe.EntityResolver;
import eu.toolchain.scribe.JavaType;
import eu.toolchain.scribe.annotations.EntityCreator;
import eu.toolchain.scribe.annotations.Property;
import eu.toolchain.scribe.typemapping.TypeMapping;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

public class CreatorMethodsTest {
  private static final JavaType STRING = JavaType.of(String.class);

  private EntityResolver resolver;
  private TypeMapping string;

  @Before
  public void setup() {
    resolver = spy(EntityMapper.nativeBuilder().build());
    string = mock(TypeMapping.class);

    doReturn(string).when(resolver).mapping(STRING);
  }

  static class BadEntity {
    public BadEntity(final String ghost) {
    }
  }

  @Test
  public void testBadEntity() {
    Optional<? extends CreatorMethod> method =
        resolver.detectCreatorMethod(JavaType.of(BadEntity.class));

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
    Optional<? extends CreatorMethod> method =
        resolver.detectCreatorMethod(JavaType.of(Constructor.class));

    assertTrue(method.isPresent());
    final CreatorMethod creator = method.get();
    assertTrue(creator instanceof ConstructorCreatorMethod);
    final ConstructorCreatorMethod c = (ConstructorCreatorMethod) creator;

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
    Optional<? extends CreatorMethod> method =
        resolver.detectCreatorMethod(JavaType.of(StaticMethod.class));

    assertTrue(method.isPresent());
    final CreatorMethod creator = method.get();
    assertTrue(creator instanceof StaticMethodCreatorMethod);
    final StaticMethodCreatorMethod c = (StaticMethodCreatorMethod) creator;

    checkFields(c);
  }

  private void checkFields(final CreatorMethod c) {
    assertEquals(2, c.getFields().size());

    assertEquals(STRING, c.getFields().get(0).getType());
    assertTrue(c.getFields().get(0).getAnnotations().isAnnotationPresent(Property.class));

    assertEquals(STRING, c.getFields().get(1).getType());
    assertTrue(c.getFields().get(1).getAnnotations().isAnnotationPresent(Property.class));
  }
}
