package eu.toolchain.ogt.typeinterceptor;

import eu.toolchain.ogt.Annotations;
import eu.toolchain.ogt.EntityResolver;
import eu.toolchain.ogt.JavaType;
import eu.toolchain.ogt.annotations.Bytes;
import eu.toolchain.ogt.type.EncodedBytesTypeMapping;
import eu.toolchain.ogt.type.EncodedForeignBytesTypeMapping;
import eu.toolchain.ogt.type.TypeMapping;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;

@RunWith(MockitoJUnitRunner.class)
public class BytesAnnotationTypeInterceptorTest {
    @Mock
    private Bytes bytes;

    @Mock
    private EntityResolver resolver;

    @Mock
    private JavaType type;

    @Test
    public void testForeignBytes() {
        doReturn(true).when(bytes).foreign();
        final Annotations annotations = Annotations.of(bytes);

        final Optional<TypeMapping> intercept =
            BytesAnnotationTypeInterceptor.intercept(resolver, type, annotations);

        assertTrue(intercept.isPresent());
        assertEquals(new EncodedForeignBytesTypeMapping(type), intercept.get());
    }

    @Test
    public void testBytes() {
        final TypeMapping mapping = Mockito.mock(TypeMapping.class);

        doReturn(false).when(bytes).foreign();
        doReturn(mapping).when(resolver).mapping(type);

        final Annotations annotations = Annotations.of(bytes);

        final Optional<TypeMapping> intercept =
            BytesAnnotationTypeInterceptor.intercept(resolver, type, annotations);

        assertTrue(intercept.isPresent());
        assertEquals(new EncodedBytesTypeMapping(mapping), intercept.get());
    }
}
