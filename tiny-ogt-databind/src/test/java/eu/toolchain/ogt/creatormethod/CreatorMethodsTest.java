package eu.toolchain.ogt.creatormethod;

import eu.toolchain.ogt.EntityMapper;
import eu.toolchain.ogt.EntityResolver;
import eu.toolchain.ogt.JavaType;
import eu.toolchain.ogt.annotations.EntityCreator;
import eu.toolchain.ogt.annotations.Property;
import eu.toolchain.ogt.type.TypeMapping;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

import static eu.toolchain.ogt.JavaType.construct;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

public class CreatorMethodsTest {
    private static final JavaType STRING = construct(String.class);

    private EntityResolver resolver;
    private TypeMapping string;

    @Before
    public void setup() {
        resolver = spy(EntityMapper.nativeBuilder().build());
        string = mock(TypeMapping.class);

        doReturn(string).when(resolver).mapping(STRING);
    }

    static class BadEntity {
        public BadEntity() {
        }
    }

    @Test
    public void testBadEntity() {
        Optional<CreatorMethod> method = resolver.detectCreatorMethod(construct(BadEntity.class));
        assertFalse(method.isPresent());
    }

    static class Constructor {
        @EntityCreator
        public Constructor(
            @Property("field") final String field, @Property("indexed") final String indexed
        ) {
        }
    }

    @Test
    public void testConstructor() {
        Optional<CreatorMethod> method = resolver.detectCreatorMethod(construct(Constructor.class));

        assertTrue(method.isPresent());
        final CreatorMethod creator = method.get();
        assertTrue(creator instanceof ConstructorCreatorMethod);
        final ConstructorCreatorMethod c = (ConstructorCreatorMethod) creator;

        checkFields(c);
    }

    static class StaticMethod {
        @EntityCreator
        public static StaticMethod build(
            @Property("field") final String field, @Property("indexed") final String indexed
        ) {
            return new StaticMethod();
        }
    }

    @Test
    public void testStaticMethod() {
        Optional<CreatorMethod> method =
            resolver.detectCreatorMethod(construct(StaticMethod.class));

        assertTrue(method.isPresent());
        final CreatorMethod creator = method.get();
        assertTrue(creator instanceof StaticMethodCreatorMethod);
        final StaticMethodCreatorMethod c = (StaticMethodCreatorMethod) creator;

        checkFields(c);
    }

    private void checkFields(final CreatorMethod c) {
        assertEquals(2, c.fields().size());

        assertEquals(Optional.of(STRING), c.fields().get(0).type());
        assertTrue(c.fields().get(0).annotations().isAnnotationPresent(Property.class));

        assertEquals(Optional.of(STRING), c.fields().get(1).type());
        assertTrue(c.fields().get(1).annotations().isAnnotationPresent(Property.class));
    }
}
