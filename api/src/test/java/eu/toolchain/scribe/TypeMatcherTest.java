package eu.toolchain.scribe;

import org.junit.Test;

import java.util.List;
import java.util.Map;

import static eu.toolchain.scribe.TypeMatcher.any;
import static eu.toolchain.scribe.TypeMatcher.type;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TypeMatcherTest {
  @Test
  public void testIsPrimitive() {
    final TypeMatcher m = TypeMatcher.isPrimitive();

    JavaType.PRIMITIVES.forEach(p -> {
      assertFalse(p.isBoxed());
      assertTrue("Primitive type " + p + " matches", m.matches(p));
      assertTrue(p.asBoxed().isBoxed());
      assertTrue("Primitive type " + p + " matches (boxed)", m.matches(p.asBoxed()));
    });
  }

  @Test
  public void testIsSpecificPrimitive() {
    final TypeMatcher m = TypeMatcher.isPrimitive(boolean.class);

    assertTrue("Unboxed primitive matches", m.matches(JavaType.of(boolean.class)));
    assertTrue("Boxed primitive matches", m.matches(JavaType.of(Boolean.class)));
  }

  @Test
  public void testGeneric() {
    final JavaType t = JavaType.of(new TypeReference<Map<String, String>>() {
    });

    assertTrue(type(Map.class, any(), any()).matches(t));
    assertTrue(type(Map.class, type(String.class), type(String.class)).matches(t));
    assertFalse(type(Map.class, type(String.class), type(List.class, any())).matches(t));
  }
}
