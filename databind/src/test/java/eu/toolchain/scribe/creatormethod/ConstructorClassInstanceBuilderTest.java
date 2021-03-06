package eu.toolchain.scribe.creatormethod;

import eu.toolchain.scribe.ClassInstanceBuilder;
import eu.toolchain.scribe.ConstructorClassInstanceBuilder;
import eu.toolchain.scribe.EntityField;
import eu.toolchain.scribe.EntityResolver;
import eu.toolchain.scribe.InstanceBuilder;
import eu.toolchain.scribe.detector.InstanceBuilderDetector;
import eu.toolchain.scribe.detector.Match;
import eu.toolchain.scribe.detector.MatchPriority;
import eu.toolchain.scribe.reflection.JavaType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;
import java.util.Optional;

import static java.util.Collections.emptyList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ConstructorClassInstanceBuilderTest {
  @Mock
  EntityResolver resolver;

  @Test
  public void testDetection() {
    final List<EntityField> fields = emptyList();

    when(resolver.detectExecutableFields(any())).thenReturn(fields);

    final InstanceBuilderDetector detector =
        ConstructorClassInstanceBuilder.forAnnotation(Marker.class);

    Optional<Match<ClassInstanceBuilder<Object>>> detected =
        detector.detect(resolver, JavaType.of(Entity.class)).findFirst();

    Match<ClassInstanceBuilder<Object>> match =
        detected.orElseThrow(() -> new AssertionError("Expected value"));

    assertEquals(MatchPriority.HIGH, match.getPriority());
    final ConstructorClassInstanceBuilder<Object> method =
        (ConstructorClassInstanceBuilder<Object>) match.getValue();

    final InstanceBuilder.Constructor<Object> builder =
        (InstanceBuilder.Constructor<Object>) method.getInstanceBuilder();

    assertEquals(fields, method.getFields());
    assertEquals(JavaType.of(Entity.class).getConstructors().findFirst().get(),
        builder.getConstructor());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNonRuntimeRetentionPolicy() {
    ConstructorClassInstanceBuilder.forAnnotation(NonRuntimeMarker.class);
  }

  static class Entity {
    @Marker
    public Entity() {
    }
  }

  @Retention(RetentionPolicy.RUNTIME)
  @interface Marker {

  }

  @Retention(RetentionPolicy.SOURCE)
  @interface NonRuntimeMarker {
  }
}
