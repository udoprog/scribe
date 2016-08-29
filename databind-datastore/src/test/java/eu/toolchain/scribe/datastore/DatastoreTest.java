package eu.toolchain.scribe.datastore;

import com.google.datastore.v1.Entity;
import com.google.datastore.v1.Value;

import eu.toolchain.scribe.DatabindOptions;
import eu.toolchain.scribe.EntityMapper;
import eu.toolchain.scribe.NativeAnnotationsModule;
import eu.toolchain.scribe.TypeReference;

import org.junit.Test;

import java.util.Optional;

import lombok.Data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DatastoreTest {
  private final DatastoreEntityMapper mapper = new DatastoreEntityMapper(EntityMapper
      .defaultBuilder()
      .register(new NativeAnnotationsModule())
      .build());

  @Test
  public void testStringField() {
    final Value value = mapper.encodingFor(String.class).encode("foo");
    assertEquals(Value.ValueTypeCase.STRING_VALUE, value.getValueTypeCase());
    assertEquals("foo", value.getStringValue());
  }

  @Test
  public void testOptionalPresent() {
    final Value value = mapper.encodingFor(new TypeReference<Optional<String>>() {
    }).encode(Optional.of("foo"));

    assertEquals(Value.ValueTypeCase.STRING_VALUE, value.getValueTypeCase());
    assertEquals("foo", value.getStringValue());
  }

  @Test
  public void testOptionalAbsent() {
    final Value value = mapper.encodingFor(new TypeReference<Optional<String>>() {
    }).encode(Optional.empty());

    assertEquals(Value.ValueTypeCase.NULL_VALUE, value.getValueTypeCase());
  }

  @Data
  public static class OptionalFieldAbsent {
    private final Optional<String> field;
  }

  @Test
  public void testOptionalFieldAbsent() {
    final Entity value = mapper.encodingFor(OptionalFieldAbsent.class).encode(
        new OptionalFieldAbsent(Optional.empty())).getEntityValue();

    assertTrue(value.getProperties().isEmpty());
  }

  @Test
  public void testOptionalFieldAbsentAsNull() {
    final Entity value = mapper.withOptions(DatabindOptions.OPTIONAL_EMPTY_AS_NULL)
        .encodingFor(OptionalFieldAbsent.class).encode(
            new OptionalFieldAbsent(Optional.empty())).getEntityValue();

    assertFalse(value.getProperties().isEmpty());
    assertEquals(Value.ValueTypeCase.NULL_VALUE,
        value.getProperties().get("field").getValueTypeCase());
  }
}
