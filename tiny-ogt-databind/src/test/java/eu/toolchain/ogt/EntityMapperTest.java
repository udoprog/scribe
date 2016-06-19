package eu.toolchain.ogt;

import com.google.common.collect.ImmutableList;
import eu.toolchain.ogt.annotations.EntityCreator;
import eu.toolchain.ogt.annotations.FieldGetter;
import eu.toolchain.ogt.annotations.Property;
import eu.toolchain.ogt.entitybinding.BuilderEntityBinding;
import eu.toolchain.ogt.entitybinding.ConstructorEntityBinding;
import eu.toolchain.ogt.type.JavaType;
import eu.toolchain.ogt.typemapping.ConcreteEntityTypeMapping;
import eu.toolchain.ogt.typemapping.EntityTypeMapping;
import eu.toolchain.ogt.typemapping.TypeMapping;
import lombok.Data;
import lombok.experimental.Builder;
import org.junit.Test;

import java.beans.ConstructorProperties;
import java.lang.reflect.Type;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class EntityMapperTest {
    private final EntityMapper mapper = EntityMapper.nativeBuilder().build();

    @Data
    @Builder
    public static class BuilderTest {
        private final String string;
    }

    @Test
    public void testBuilder() {
        final EntityTypeMapping a =
            (EntityTypeMapping) mapper.mapping(JavaType.of(BuilderTest.class));
        assertTrue(a instanceof ConcreteEntityTypeMapping);
        final ConcreteEntityTypeMapping ca = (ConcreteEntityTypeMapping) a;
        assertTrue(ca.getBinding() instanceof BuilderEntityBinding);
    }

    @Data
    @Builder
    public static class EntityCreatorTest {
        private final String string;

        @EntityCreator
        public EntityCreatorTest(@Property("string") final String string) {
            this.string = string;
        }
    }

    @Test
    public void testEntityCreator() {
        final EntityTypeMapping a =
            (EntityTypeMapping) mapper.mapping(JavaType.of(EntityCreatorTest.class));
        assertTrue(a instanceof ConcreteEntityTypeMapping);
        final ConcreteEntityTypeMapping ca = (ConcreteEntityTypeMapping) a;
        assertTrue(ca.getBinding() instanceof ConstructorEntityBinding);
    }

    @Test
    public void testPrimitiveTypes() {
        final List<Type> types =
            ImmutableList.of(boolean.class, byte.class, char.class, short.class, int.class,
                long.class, float.class, double.class);

        for (final Type t : types) {
            final TypeMapping m = mapper.mapping(JavaType.of(t));
            assertTrue(m instanceof EncodedTypeMapping);
        }
    }

    @Test
    public void testBoxedPrimitiveTypes() {
        final List<Type> types =
            ImmutableList.of(Boolean.class, Byte.class, Character.class, Short.class, Integer.class,
                Long.class, Float.class, Double.class);

        for (final Type t : types) {
            final TypeMapping m = mapper.mapping(JavaType.of(t));
            assertTrue(m instanceof EncodedTypeMapping);
        }
    }

    public static class FieldGetterTest {
        private final int foo;

        @ConstructorProperties({"foo"})
        public FieldGetterTest(final int field) {
            this.foo = field;
        }

        @FieldGetter("foo")
        public int getTheThing() {
            return foo;
        }
    }
}
