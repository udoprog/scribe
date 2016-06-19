package eu.toolchain.ogt;

import lombok.Data;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

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

        final JavaType.Class typeVariable = type.asClass().getTypeParameter(0).get().asClass();

        final JavaType.Class field =
            type.asClass().getField("field").get().getFieldType().asClass();

        final JavaType.Class nestedField = type
            .asClass()
            .getField("nested")
            .get()
            .getFieldType()
            .asClass()
            .getField("field")
            .get()
            .getFieldType()
            .asClass();

        assertEquals(String.class, typeVariable.getType());
        assertEquals(String.class, field.getType());
        assertEquals(String.class, nestedField.getType());
    }
}
