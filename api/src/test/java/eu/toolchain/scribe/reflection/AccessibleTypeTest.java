package eu.toolchain.scribe.reflection;

import eu.toolchain.scribe.reflection.AccessibleType;
import org.junit.Test;

import java.lang.reflect.Modifier;

import static org.junit.Assert.assertTrue;

public class AccessibleTypeTest {
  @Test
  public void testAccessors() {
    final AccessibleType type = new AccessibleType() {
      @Override
      public int getModifiers() {
        return Modifier.PUBLIC | Modifier.STATIC | Modifier.ABSTRACT;
      }
    };

    assertTrue(type.isPublic());
    assertTrue(type.isStatic());
    assertTrue(type.isAbstract());
  }
}
