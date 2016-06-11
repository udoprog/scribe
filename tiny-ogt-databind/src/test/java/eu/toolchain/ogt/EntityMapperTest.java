package eu.toolchain.ogt;

import eu.toolchain.ogt.annotations.Bytes;
import eu.toolchain.ogt.annotations.EntityCreator;
import eu.toolchain.ogt.annotations.Property;
import eu.toolchain.ogt.binding.BuilderEntityBinding;
import eu.toolchain.ogt.binding.ConstructorEntityBinding;
import eu.toolchain.ogt.binding.FieldMapping;
import eu.toolchain.ogt.type.ConcreteEntityTypeMapping;
import eu.toolchain.ogt.type.EntityTypeMapping;
import lombok.Data;
import lombok.experimental.Builder;
import org.junit.Test;

import java.beans.ConstructorProperties;
import java.util.List;

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
    public void testComplexEntity() {
        final ConcreteEntityTypeMapping mapping =
            (ConcreteEntityTypeMapping) mapper.mapping(ComplexEntity.class);

        System.out.println(mapping);
    }

    @Data
    public static class BuilderTypeCasting {
        private final Integer field;

        @ConstructorProperties({"field"})
        public BuilderTypeCasting(final int field) {
            this.field = field;
        }
    }

    @Test
    public void testBuilderTypeCasting() {
        final ConcreteEntityTypeMapping mapping =
            (ConcreteEntityTypeMapping) mapper.mapping(BuilderTypeCasting.class);

        List<? extends FieldMapping> fields = mapping.getBinding().fields();

        System.out.println(mapping);
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

    @Data
    public static class OtherEntity {
        private final int field;
    }

    public static class OtherForeignEntity {
        private final int field;

        public OtherForeignEntity(final int field) {
            this.field = field;
        }
    }

    @Data
    public static class ComplexEntity {
        @Bytes
        private final OtherEntity bytes;

        @Bytes(foreign = true)
        private final OtherForeignEntity bytesForeign;

        private final boolean primitiveBoolean;
        private final Boolean boxedBoolean;

        private final byte primitiveByte;
        private final Byte boxedByte;

        private final char primitiveChar;
        private final Character boxedCharacter;

        private final short primitiveShort;
        private final Short boxedShort;

        private final int primitiveInt;
        private final Integer boxedInt;

        private final long primitiveLong;
        private final Long boxedLong;

        private final float primitiveFloat;
        private final Float boxedFloat;

        private final double primitiveDouble;
        private final Double boxedDouble;

        private final byte[] byteArray;

        private final OtherEntity[] otherEntityArray;
        private final OtherEntity[][] otherEntityArrayArray;
    }
}
