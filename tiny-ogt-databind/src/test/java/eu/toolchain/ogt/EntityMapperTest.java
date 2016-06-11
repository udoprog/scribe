package eu.toolchain.ogt;

import com.google.common.collect.ImmutableList;
import eu.toolchain.ogt.annotations.EntityCreator;
import eu.toolchain.ogt.annotations.FieldGetter;
import eu.toolchain.ogt.annotations.Property;
import eu.toolchain.ogt.binding.BuilderEntityBinding;
import eu.toolchain.ogt.binding.ConstructorEntityBinding;
import eu.toolchain.ogt.binding.FieldMapping;
import eu.toolchain.ogt.type.BoxedPrimitiveTypeMapping;
import eu.toolchain.ogt.type.ConcreteEntityTypeMapping;
import eu.toolchain.ogt.type.EntityTypeMapping;
import eu.toolchain.ogt.type.PrimitiveTypeMapping;
import eu.toolchain.ogt.type.TypeMapping;
import lombok.Data;
import lombok.experimental.Builder;
import org.junit.Test;

import java.beans.ConstructorProperties;
import java.util.List;

import static eu.toolchain.ogt.JavaType.construct;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class EntityMapperTest {
    private final EntityMapper mapper = EntityMapper.nativeBuilder().build();

    @Test
    public void testBuilder() {
        final EntityTypeMapping a = (EntityTypeMapping) mapper.mapping(BuilderTest.class);
        assertTrue(a instanceof ConcreteEntityTypeMapping);
        final ConcreteEntityTypeMapping ca = (ConcreteEntityTypeMapping) a;
        assertTrue(ca.getBinding() instanceof BuilderEntityBinding);
    }

    @Test
    public void testEntityCreator() {
        final EntityTypeMapping a = (EntityTypeMapping) mapper.mapping(EntityCreatorTest.class);
        assertTrue(a instanceof ConcreteEntityTypeMapping);
        final ConcreteEntityTypeMapping ca = (ConcreteEntityTypeMapping) a;
        assertTrue(ca.getBinding() instanceof ConstructorEntityBinding);
    }

    @Test
    public void testPrimitiveTypes() {
        final List<JavaType> types =
            ImmutableList.of(construct(boolean.class), construct(byte.class), construct(char.class),
                construct(short.class), construct(int.class), construct(long.class),
                construct(float.class), construct(double.class));

        for (final JavaType t : types) {
            final TypeMapping m = mapper.mapping(t);
            assertTrue(m instanceof PrimitiveTypeMapping);
            final PrimitiveTypeMapping p = (PrimitiveTypeMapping) m;
            assertEquals(t, p.getType());
        }
    }

    @Test
    public void testBoxedPrimitiveTypes() {
        final List<JavaType> types =
            ImmutableList.of(construct(Boolean.class), construct(Byte.class),
                construct(Character.class), construct(Short.class), construct(Integer.class),
                construct(Long.class), construct(Float.class), construct(Double.class));

        for (final JavaType t : types) {
            final TypeMapping m = mapper.mapping(t);
            assertTrue(m instanceof BoxedPrimitiveTypeMapping);
            final BoxedPrimitiveTypeMapping p = (BoxedPrimitiveTypeMapping) m;
            assertEquals(t, p.getType());
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

    @Test
    public void testFieldGetter() {
        final ConcreteEntityTypeMapping mapping =
            (ConcreteEntityTypeMapping) mapper.mapping(FieldGetterTest.class);

        FieldMapping field = mapping.getBinding().fields().get(0);

        assertEquals(42, field.reader().read(new FieldGetterTest(42)));
    }

    @Data
    @Builder
    public static class BuilderTest {
        private final String string;
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
}
