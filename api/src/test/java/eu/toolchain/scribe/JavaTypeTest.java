package eu.toolchain.scribe;

import lombok.Data;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class JavaTypeTest {
  @Data
  static class TestGenericTypeVariable<T> {
    private final T field;
    private final Nested nested;

    class Nested {
      private T field;
    }
  }

  @Test
  public void testGenericTypeVariable() {
    final JavaType type = JavaType.of(new TypeReference<TestGenericTypeVariable<String>>() {
    });

    final JavaType typeVariable = type.getTypeParameter(0).get();

    final JavaType field = type.getField("field").get().getFieldType();

    final JavaType nestedField = type
        .getField("nested")
        .flatMap(nested -> nested.getFieldType().getField("field"))
        .get()
        .getFieldType();

    assertEquals(String.class, typeVariable.getType());
    assertEquals(String.class, field.getType());
    assertEquals(String.class, nestedField.getType());
  }

  @Test
  public void testPrimitive() {
    for (final JavaType p : JavaType.PRIMITIVES) {
      final JavaType b = JavaType.of(p.getType());
      assertEquals(0, b.getTypeParameters().size());
      assertEquals(p.getType(), b.getType());
      assertTrue(b.isPrimitive());
    }
  }

  @Data
  public static class TestMethods {
    private final String field1;
    private final int field2;
  }

  @Test
  public void testMethods() {
    final JavaType type = JavaType.of(TestMethods.class);

    JavaType.Method field1 = type.getMethod("getField1").findFirst().get();
    assertEquals(JavaType.of(String.class), field1.getReturnType());

    JavaType.Method field2 = type.getMethod("getField2").findFirst().get();
    assertEquals(JavaType.of(int.class), field2.getReturnType());
  }

  @Data
  static class TestAssertEquals1<E, T extends List<E>> {
    private final T field;
  }

  @Data
  static class TestAssertEquals2<E, T extends List<E>> {
    private final T field;
  }

  @Test
  public void testAssertEquals() {
    final JavaType t1 = JavaType.of(new TypeReference<TestAssertEquals1<String, List<String>>>() {
    }).getField("field").get().getFieldType();

    final JavaType t2 = JavaType.of(new TypeReference<TestAssertEquals2<String, List<String>>>() {
    }).getField("field").get().getFieldType();

    assertEquals(t1, t2);
    assertEquals(t1.getTypeParameters().get(0), JavaType.of(String.class));
  }

  static class TestFindMethod {
    public void empty() {
    }

    public void one(String first) {
    }
  }

  @Test
  public void testFindMethod() {
    final JavaType type = JavaType.of(TestFindMethod.class);

    assertNotNull(type.getMethod("empty"));
    assertNotNull(type.getMethod("one", JavaType.of(String.class)));
  }

  static class TestGeneric<T> {
    public void empty(T parameter) {
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void testGeneric() {
    JavaType.of(TestGeneric.class);
  }

  @Test
  public void testGenericWithType() {
    final JavaType type = JavaType.of(new TypeReference<TestGeneric<String>>() {
    });

    JavaType.Method empty = type.getMethod("empty", JavaType.of(String.class)).findAny().get();

    final JavaType.Parameter p = empty.getParameters().get(0);
    assertEquals(JavaType.of(String.class), p.getParameterType());
    assertEquals(JavaType.of(void.class), empty.getReturnType());
  }

  private final JavaType typeA = JavaType.of(EqualityTypeA.class);
  private final JavaType typeB = JavaType.of(EqualityTypeB.class);

  @Test
  public void testConstructorEquality() throws Exception {
    // Identical constructors from different types are not equal in the JDK.
    assertNotEquals(EqualityTypeA.class.getConstructor(int.class),
        EqualityTypeB.class.getConstructor(int.class));

    assertEquals(typeA.getConstructor(JavaType.of(int.class)),
        typeB.getConstructor(JavaType.of(int.class)));
  }

  @Test
  public void testMethodEquality() throws Exception {
    // Identical methods from different types are not equal in the JDK.
    assertNotEquals(EqualityTypeA.class.getMethod("methodOne"),
        EqualityTypeB.class.getMethod("methodTwo"));

    assertEquals(typeA.getMethod("methodOne").findFirst(),
        typeB.getMethod("methodOne").findFirst());

    assertNotEquals(typeA.getMethod("methodOne").findFirst(),
        typeB.getMethod("methodTwo").findFirst());
  }

  @Test
  public void testFieldEquality() throws Exception {
    // Identical fields from different types are not equal in the JDK.
    assertNotEquals(EqualityTypeA.class.getDeclaredField("fieldOne"),
        EqualityTypeB.class.getDeclaredField("fieldOne"));

    assertEquals(typeA.getField("fieldOne"), typeB.getField("fieldOne"));

    assertNotEquals(typeA.getField("fieldOne"), typeB.getField("fieldTwo"));
  }

  static class EqualityTypeA {
    private final int fieldOne;

    public EqualityTypeA(int fieldOne) {
      this.fieldOne = fieldOne;
    }

    public boolean methodOne() {
      return false;
    }
  }

  static class EqualityTypeB {
    private final int fieldOne;
    private final int fieldTwo;

    public EqualityTypeB(int fieldOne) {
      this.fieldOne = fieldOne;
      this.fieldTwo = fieldOne;
    }

    public boolean methodOne() {
      return false;
    }

    public int methodTwo() {
      return 0;
    }
  }
}
