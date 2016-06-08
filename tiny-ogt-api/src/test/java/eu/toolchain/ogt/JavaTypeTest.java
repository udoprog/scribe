package eu.toolchain.ogt;

import lombok.Data;
import lombok.experimental.Builder;
import org.junit.Test;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import static eu.toolchain.ogt.JavaType.construct;
import static eu.toolchain.ogt.JavaType.of;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class JavaTypeTest {
    @Test
    public void testBoxed() {
        assertEquals(construct(Boolean.class), construct(boolean.class).boxed());
        assertEquals(construct(Byte.class), construct(byte.class).boxed());
        assertEquals(construct(Character.class), construct(char.class).boxed());
        assertEquals(construct(Short.class), construct(short.class).boxed());
        assertEquals(construct(Integer.class), construct(int.class).boxed());
        assertEquals(construct(Long.class), construct(long.class).boxed());
        assertEquals(construct(Float.class), construct(float.class).boxed());
        assertEquals(construct(Double.class), construct(double.class).boxed());
    }

    /* references for generic types */
    @Data
    static class ParmeterizedTypes {
        Map<String, String> map1;
        Map<? extends String, ? extends String> map2;

        Set<String> set1;
        Set<? extends String> set2;
        SortedSet<String> set3;

        List<Object> listObjects;
        Object[] arrayObjects;
        Object object;
    }

    @Test
    public void testParameterizedTypes() throws Exception {
        final JavaType map1 = of(Map.class, of(String.class), of(String.class));

        assertEquals(map1, type("map1", ParmeterizedTypes.class));
        assertEquals(map1, type("map2", ParmeterizedTypes.class));

        final JavaType set1 = of(Set.class, of(String.class));

        assertEquals(set1, type("set1", ParmeterizedTypes.class));
        assertEquals(set1, type("set2", ParmeterizedTypes.class));
        assertNotEquals(set1, type("set3", ParmeterizedTypes.class));

        assertEquals(of(List.class, of(Object.class)),
            type("listObjects", ParmeterizedTypes.class));
        assertEquals(of(Object[].class), type("arrayObjects", ParmeterizedTypes.class));
        assertEquals(of(Object.class), type("object", ParmeterizedTypes.class));
    }

    /* test type variables */
    @Data
    static class TypeVariables<T, L extends List<?>> {
        T object;
        L list;
    }

    /**
     * Test various type boundaries.
     */
    @Test
    public void testTypeVariables() throws Exception {
        assertEquals(of(List.class, of(Object.class)), type("list", TypeVariables.class));
        assertEquals(of(Object.class), type("object", TypeVariables.class));
    }

    @Builder
    @Data
    static class Arrays<T, L extends List<?>, LL extends List<List<?>>> {
        T[] object;
        L[] list;
        LL[] listList;
    }

    @Test
    public void testArrays() throws Exception {
        assertEquals(of(Object[].class), type("object", Arrays.class));
        assertEquals(of(List[].class), type("list", Arrays.class));
        assertEquals(of(List[].class), type("listList", Arrays.class));
    }

    static class IllegalBoundaries<T extends Serializable & Comparable<T>> {
        T illegal;
    }

    /**
     * When multiple type boundaries are present we can't determine a legal single type.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testIllegalBoundaries() throws Exception {
        type("illegal", IllegalBoundaries.class);
    }

    static JavaType type(String field, Class<?> source) throws Exception {
        return construct(source.getDeclaredField(field).getGenericType());
    }
}
