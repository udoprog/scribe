package eu.toolchain.ogt;

import org.junit.Test;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import static eu.toolchain.ogt.TypeMatcher.any;
import static eu.toolchain.ogt.TypeMatcher.exact;
import static eu.toolchain.ogt.TypeMatcher.parameterized;
import static eu.toolchain.ogt.TypeMatcher.lower;
import static eu.toolchain.ogt.TypeMatcher.upper;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TypeMatcherTest {
    static class TestGeneric {
        private Map<String, String> field;
    }

    @Test
    public void testGeneric() {
        final Type type = type("field", TestGeneric.class);

        assertTrue(parameterized(Map.class, any(), any()).matches(type));
        assertTrue(parameterized(Map.class, exact(String.class), exact(String.class)).matches(type));
        assertFalse(
            parameterized(Map.class, exact(String.class), parameterized(List.class, any())).matches(type));
    }

    static class TestGenericWildcard {
        private Map<String, ?> field;
        private Map<String, ? extends String> field2;
        private Map<String, ? super String> field3;
    }

    @Test
    public void testGenericWildcard1() {
        final Type field = type("field", TestGenericWildcard.class);

        assertTrue(
            parameterized(Map.class, exact(String.class), upper(exact(Object.class))).matches(field));
    }

    @Test
    public void testGenericWildcard2() {
        final Type field2 = type("field2", TestGenericWildcard.class);

        assertTrue(parameterized(Map.class, any(), upper(exact(String.class))).matches(field2));
    }

    @Test
    public void testGenericWildcard3() {
        final Type field2 = type("field3", TestGenericWildcard.class);

        assertTrue(parameterized(Map.class, any(), lower(exact(String.class))).matches(field2));
    }

    static Type type(String field, Class<?> source) {
        try {
            return source.getDeclaredField(field).getGenericType();
        } catch (NoSuchFieldException e) {
            throw new IllegalArgumentException(field, e);
        }
    }
}
