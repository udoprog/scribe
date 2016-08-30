package eu.toolchain.scribe.datastore;

import com.google.datastore.v1.Entity;
import com.google.datastore.v1.Key;
import com.google.datastore.v1.Value;
import com.google.protobuf.NullValue;
import eu.toolchain.scribe.DatabindOptions;
import eu.toolchain.scribe.EntityMapper;
import eu.toolchain.scribe.NativeAnnotationsModule;
import eu.toolchain.scribe.TypeReference;
import lombok.Data;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

import static eu.toolchain.scribe.Matchers.mappingException;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class DatastoreTest {
  private final DatastoreEntityMapper mapper = new DatastoreEntityMapper(EntityMapper
      .defaultBuilder()
      .install(new DatastoreModule())
      .install(new NativeAnnotationsModule())
      .build());

  private final Key key =
      Key.newBuilder().addPath(Key.PathElement.newBuilder().setKind("foo").build()).build();

  @Rule
  public ExpectedException exception = ExpectedException.none();

  @Data
  public static class OptionalField {
    private final Optional<String> field;
  }

  @Test
  public void testOptional() {
    testValue(OptionalField.class, new OptionalField(Optional.empty()), (b, v) -> {
      return b.setEntityValue(Entity.newBuilder().build());
    });

    testValue(OptionalField.class, new OptionalField(Optional.of("foo")), (b, v) -> {
      final Entity.Builder entity = Entity.newBuilder();
      final Map<String, Value> p = entity.getMutableProperties();

      p.put("field", Value.newBuilder().setStringValue(v.getField().get()).build());

      return b.setEntityValue(entity.build());
    });
  }

  @Test
  public void testOptionalFieldAbsentAsNull() {
    testValue(OptionalField.class, new OptionalField(Optional.empty()), (b, v) -> {
      final Entity.Builder entity = Entity.newBuilder();
      final Map<String, Value> p = entity.getMutableProperties();

      p.put("field", Value.newBuilder().setNullValue(NullValue.NULL_VALUE).build());

      return b.setEntityValue(entity.build());
    }, m -> m.withOptions(DatabindOptions.OPTIONAL_EMPTY_AS_NULL));

    testValue(OptionalField.class, new OptionalField(Optional.of("foo")), (b, v) -> {
      final Entity.Builder entity = Entity.newBuilder();
      final Map<String, Value> p = entity.getMutableProperties();

      p.put("field", Value.newBuilder().setStringValue(v.getField().get()).build());

      return b.setEntityValue(entity.build());
    }, m -> m.withOptions(DatabindOptions.OPTIONAL_EMPTY_AS_NULL));
  }

  @Data
  public static class ValueEntity {
    private final Value value;
    private final String name;
  }

  @Test
  public void testValueEntity() {
    final Value value = Value.newBuilder().setNullValue(NullValue.NULL_VALUE).build();

    testValue(ValueEntity.class, new ValueEntity(value, "foo"), (b, v) -> {
      final Entity.Builder entity = Entity.newBuilder();
      final Map<String, Value> p = entity.getMutableProperties();

      p.put("value", v.getValue());
      p.put("name", Value.newBuilder().setStringValue(v.getName()).build());

      return b.setEntityValue(entity.build());
    });
  }

  @Data
  public static class IndexedField {
    @ExcludeFromIndexes
    private final String field;
  }

  @Test
  public void testFieldExcludedFromIndexes() {
    testValue(IndexedField.class, new IndexedField("foo"), (b, v) -> {
      final Entity.Builder entity = Entity.newBuilder();
      final Map<String, Value> p = entity.getMutableProperties();

      p.put("field",
          Value.newBuilder().setStringValue(v.getField()).setExcludeFromIndexes(true).build());

      return b.setEntityValue(entity.build());
    });
  }

  @Data
  public static class KeyEntity {
    @EntityKey
    private final Key key;
  }

  @Test
  public void testKeyEntity() {
    testValue(KeyEntity.class, new KeyEntity(key), (b, v) -> {
      return b.setEntityValue(Entity.newBuilder().setKey(key).build());
    });
  }

  @Data
  public static class OptionalKeyEntity {
    @EntityKey
    private final Optional<Key> key;
  }

  @Test
  public void testOptionalKeyEntity() {
    testValue(OptionalKeyEntity.class, new OptionalKeyEntity(Optional.of(key)), (b, v) -> {
      return b.setEntityValue(Entity.newBuilder().setKey(key).build());
    });

    testValue(OptionalKeyEntity.class, new OptionalKeyEntity(Optional.empty()), (b, v) -> {
      return b.setEntityValue(Entity.newBuilder().build());
    });
  }

  @Test
  public void testSimpleValues() {
    /* bytes are base64 encoded */
    testValue(Byte.class, (byte) 42, (b, v) -> {
      return b.setStringValue("Kg==");
    });

    testValue(String.class, "foo", Value.Builder::setStringValue);
    testValue(Boolean.class, true, Value.Builder::setBooleanValue);

    testIntegerNumber(Short.class, (short) 42, Short::longValue);
    testIntegerNumber(Integer.class, 42, Integer::longValue);
    testIntegerNumber(Long.class, 42L, Long::longValue);

    testDoubleNumber(Float.class, 42F, Float::doubleValue);
    testDoubleNumber(Double.class, 42D, Double::doubleValue);
  }

  private <T extends Number> void testIntegerNumber(
      Class<T> cls, T number, Function<T, Long> converter
  ) {
    testValue(cls, number, (b, v) -> {
      return b.setIntegerValue(converter.apply(number));
    });
  }

  private <N extends Number> void testDoubleNumber(
      Class<N> cls, N number, Function<N, Double> converter
  ) {
    testValue(cls, number, (b, v) -> {
      return b.setDoubleValue(converter.apply(number));
    });
  }

  private <T> void testValue(
      Class<T> cls, T value, BiFunction<Value.Builder, T, Value.Builder> converter
  ) {
    testValue(cls, value, converter, Function.identity());
  }

  private <T> void testValue(
      Class<T> cls, T value, BiFunction<Value.Builder, T, Value.Builder> converter,
      Function<DatastoreEntityMapper, DatastoreEntityMapper> modifier
  ) {
    final DatastoreEncoding<T> encoding = modifier.apply(mapper).encodingFor(cls);
    final Value expected = converter.apply(Value.newBuilder(), value).build();

    assertThat(encoding.encode(value), is(expected));
    assertThat(encoding.decode(expected), is(value));
  }

  private <T> void testValue(
      TypeReference<T> type, T value, BiFunction<Value.Builder, T, Value.Builder> converter
  ) {
    testValue(type, value, converter, Function.identity());
  }

  private <T> void testValue(
      TypeReference<T> type, T value, BiFunction<Value.Builder, T, Value.Builder> converter,
      Function<DatastoreEntityMapper, DatastoreEntityMapper> modifier
  ) {
    final DatastoreEncoding<T> encoding = modifier.apply(mapper).encodingFor(type);
    final Value expected = converter.apply(Value.newBuilder(), value).build();

    assertThat(encoding.encode(value), is(expected));
    assertThat(encoding.decode(expected), is(value));
  }

  @Data
  public static class MissingField {
    private final String field;
  }

  @Test
  public void testMissingField() {
    exception.expect(mappingException("field"));

    final DatastoreEncoding<MissingField> encoding = mapper.encodingFor(MissingField.class);
    final Value value = Value.newBuilder().setEntityValue(Entity.newBuilder().build()).build();

    encoding.decode(value);
  }
}
