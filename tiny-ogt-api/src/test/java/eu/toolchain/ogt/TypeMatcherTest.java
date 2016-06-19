package eu.toolchain.ogt;

import eu.toolchain.ogt.type.JavaType;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static eu.toolchain.ogt.TypeMatcher.any;
import static eu.toolchain.ogt.TypeMatcher.exact;
import static eu.toolchain.ogt.TypeMatcher.parameterized;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TypeMatcherTest {
    static class TestGeneric {
        private Map<String, String> field;
    }

    @Test
    public void testGeneric() {
        final JavaType type = type("field", TestGeneric.class);

        assertTrue(parameterized(Map.class, any(), any()).matches(type));
        assertTrue(
            parameterized(Map.class, exact(String.class), exact(String.class)).matches(type));
        assertFalse(
            parameterized(Map.class, exact(String.class), parameterized(List.class, any())).matches(
                type));
    }

    static JavaType type(String field, Class<?> source) {
        try {
            return JavaType.of(source.getDeclaredField(field).getGenericType());
        } catch (NoSuchFieldException e) {
            throw new IllegalArgumentException(field, e);
        }
    }
}
