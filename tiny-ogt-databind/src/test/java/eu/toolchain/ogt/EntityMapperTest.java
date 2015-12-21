package eu.toolchain.ogt;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

import eu.toolchain.ogt.annotations.EntityCreator;
import eu.toolchain.ogt.annotations.Property;
import eu.toolchain.ogt.binding.BuilderBinding;
import eu.toolchain.ogt.binding.ConstructorBinding;
import eu.toolchain.ogt.type.ConcreteEntityTypeMapping;
import eu.toolchain.ogt.type.EntityTypeMapping;
import lombok.Data;
import lombok.experimental.Builder;

public class EntityMapperTest {
    private final EntityMapper mapper = EntityMapper.nativeBuilder().build();

    @Test
    public void testBuilder() {
        final EntityTypeMapping a = (EntityTypeMapping) mapper.mapping(BuilderTest.class);
        assertTrue(a instanceof ConcreteEntityTypeMapping);
        final ConcreteEntityTypeMapping ca = (ConcreteEntityTypeMapping) a;
        assertTrue(ca.getBinder() instanceof BuilderBinding);
    }

    @Test
    public void testEntityCreator() {
        final EntityTypeMapping a = (EntityTypeMapping) mapper.mapping(EntityCreatorTest.class);
        assertTrue(a instanceof ConcreteEntityTypeMapping);
        final ConcreteEntityTypeMapping ca = (ConcreteEntityTypeMapping) a;
        assertTrue(ca.getBinder() instanceof ConstructorBinding);
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
