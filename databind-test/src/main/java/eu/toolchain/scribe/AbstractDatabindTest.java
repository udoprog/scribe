package eu.toolchain.scribe;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import eu.toolchain.scribe.annotations.EntityCreator;
import eu.toolchain.scribe.annotations.EntitySubTypes;
import eu.toolchain.scribe.annotations.EntityTypeName;
import eu.toolchain.scribe.annotations.EntityValue;
import lombok.Data;
import lombok.experimental.Builder;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;

public abstract class AbstractDatabindTest {
  @Rule
  public ExpectedException exception = ExpectedException.none();

  private static final TypeReference<Map<String, Optional<String>>> OPTIONAL_STRING_MAP =
      new TypeReference<Map<String, Optional<String>>>() {
      };

  private static final Entry E1 = new Entry(10.10D);
  private static final Entry E2 = new Entry(20.20D);

  private static final List<Entry> LIST = ImmutableList.of(E1, E2);
  private static final Map<String, Entry> MAP = ImmutableMap.of("hello", E1, "hello2", E2);

  private static final byte[] BYTE_ARRAY =
      new byte[]{(byte) 0xde, (byte) 0xad, (byte) 0xbe, (byte) 0xef};

  protected abstract <S> StringEncoding<S> encodingFor(TypeReference<S> type, Option... options);

  protected abstract <S> StringEncoding<S> encodingFor(Class<S> type, Option... options);

  @Before
  public void setup() throws Exception {
  }

  @Data
  public static class ListField {
    private final List<Entry> field;
  }

  @Test
  public void testListField() throws Exception {
    roundTrip(encodingFor(ListField.class), new ListField(ImmutableList.of(E1)));
  }

  @Data
  public static class MapField {
    private final Map<String, Entry> field;
  }

  @Test
  public void testMapField() {
    roundTrip(encodingFor(MapField.class), new MapField(ImmutableMap.of("e1", E1, "e2", E2)));
  }

  @Data
  public static class OptionalField {
    private final Optional<Entry> field;
  }

  @Test
  public void testOptionalField() {
    final StringEncoding<OptionalField> e = encodingFor(OptionalField.class);
    roundTrip(e, new OptionalField(Optional.empty()));
    roundTrip(e, new OptionalField(Optional.of(E1)));
  }

  @Data
  public static class StringField {
    private final String field;
  }

  @Test
  public void testStringField() {
    final StringEncoding<StringField> e = encodingFor(StringField.class);
    roundTrip(e, new StringField("foo"));
  }

  @Data
  public static class BooleanField {
    private final boolean field;
  }

  @Test
  public void testBooleanField() {
    final StringEncoding<BooleanField> e = encodingFor(BooleanField.class);
    roundTrip(e, new BooleanField(true));
    roundTrip(e, new BooleanField(false));
  }

  @Data
  public static class BoxedBooleanField {
    private final Boolean field;
  }

  @Test
  public void testBoxedBooleanField() {
    final StringEncoding<BoxedBooleanField> e = encodingFor(BoxedBooleanField.class);
    roundTrip(e, new BoxedBooleanField(true));
    roundTrip(e, new BoxedBooleanField(false));
  }

  @Data
  public static class ByteField {
    private final byte field;
  }

  @Test
  public void testByteField() {
    final StringEncoding<ByteField> e = encodingFor(ByteField.class);
    roundTrip(e, new ByteField(Byte.MAX_VALUE));
    roundTrip(e, new ByteField(Byte.MIN_VALUE));
  }

  @Data
  public static class BoxedByteField {
    private final Byte field;
  }

  @Test
  public void testBoxedByteField() {
    final StringEncoding<BoxedByteField> e = encodingFor(BoxedByteField.class);
    roundTrip(e, new BoxedByteField(Byte.MAX_VALUE));
    roundTrip(e, new BoxedByteField(Byte.MIN_VALUE));
  }

  @Data
  public static class CharacterField {
    private final char field;
  }

  @Test
  public void testCharacterField() {
    final StringEncoding<CharacterField> e = encodingFor(CharacterField.class);
    roundTrip(e, new CharacterField(Character.MAX_VALUE));
    roundTrip(e, new CharacterField(Character.MIN_VALUE));
  }

  @Data
  public static class BoxedCharacterField {
    private final Character field;
  }

  @Test
  public void testBoxedCharacterField() {
    final StringEncoding<BoxedCharacterField> e = encodingFor(BoxedCharacterField.class);
    roundTrip(e, new BoxedCharacterField(Character.MAX_VALUE));
    roundTrip(e, new BoxedCharacterField(Character.MIN_VALUE));
  }

  @Data
  public static class ShortField {
    private final short field;
  }

  @Test
  public void testShortField() {
    final StringEncoding<ShortField> e = encodingFor(ShortField.class);
    roundTrip(e, new ShortField(Short.MAX_VALUE));
    roundTrip(e, new ShortField(Short.MIN_VALUE));
  }

  @Data
  public static class BoxedShortField {
    private final Short field;
  }

  @Test
  public void testBoxedShortField() {
    final StringEncoding<BoxedShortField> e = encodingFor(BoxedShortField.class);
    roundTrip(e, new BoxedShortField(Short.MAX_VALUE));
    roundTrip(e, new BoxedShortField(Short.MIN_VALUE));
  }

  @Data
  public static class IntegerField {
    private final int field;
  }

  @Test
  public void testIntegerField() {
    final StringEncoding<IntegerField> e = encodingFor(IntegerField.class);
    roundTrip(e, new IntegerField(Integer.MAX_VALUE));
    roundTrip(e, new IntegerField(Integer.MIN_VALUE));
  }

  @Data
  public static class BoxedIntegerField {
    private final Integer field;
  }

  @Test
  public void testBoxedIntegerField() {
    final StringEncoding<BoxedIntegerField> e = encodingFor(BoxedIntegerField.class);
    roundTrip(e, new BoxedIntegerField(Integer.MAX_VALUE));
    roundTrip(e, new BoxedIntegerField(Integer.MIN_VALUE));
  }

  @Data
  public static class LongField {
    private final long field;
  }

  @Test
  public void testLongField() {
    final StringEncoding<LongField> e = encodingFor(LongField.class);
    roundTrip(e, new LongField(Long.MAX_VALUE));
    roundTrip(e, new LongField(Long.MIN_VALUE));
  }

  @Data
  public static class BoxedLongField {
    private final Long field;
  }

  @Test
  public void testBoxedLongField() {
    final StringEncoding<BoxedLongField> e = encodingFor(BoxedLongField.class);
    roundTrip(e, new BoxedLongField(Long.MAX_VALUE));
    roundTrip(e, new BoxedLongField(Long.MIN_VALUE));
  }

  @Data
  public static class FloatField {
    private final float field;
  }

  @Test
  public void testFloatField() {
    final StringEncoding<FloatField> e = encodingFor(FloatField.class);
    roundTrip(e, new FloatField(Float.MAX_VALUE));
    roundTrip(e, new FloatField(Float.MIN_VALUE));
  }

  @Data
  public static class BoxedFloatField {
    private final Float field;
  }

  @Test
  public void testBoxedFloatField() {
    final StringEncoding<BoxedFloatField> e = encodingFor(BoxedFloatField.class);
    roundTrip(e, new BoxedFloatField(Float.MAX_VALUE));
    roundTrip(e, new BoxedFloatField(Float.MIN_VALUE));
  }

  @Data
  public static class DoubleField {
    private final double field;
  }

  @Test
  public void testDoubleField() {
    final StringEncoding<DoubleField> e = encodingFor(DoubleField.class);
    roundTrip(e, new DoubleField(Double.MIN_VALUE));
    roundTrip(e, new DoubleField(Double.MAX_VALUE));
  }

  @Data
  public static class BoxedDoubleField {
    private final Double field;
  }

  @Test
  public void testBoxedDoubleField() {
    final StringEncoding<BoxedDoubleField> e = encodingFor(BoxedDoubleField.class);
    roundTrip(e, new BoxedDoubleField(Double.MIN_VALUE));
    roundTrip(e, new BoxedDoubleField(Double.MAX_VALUE));
  }

  @Data
  @Builder
  public static class ByteArray {
    private final byte[] field;
  }

  @Test
  public void testByteArray() {
    final StringEncoding<ByteArray> e = encodingFor(ByteArray.class);
    roundTrip(e, new ByteArray(BYTE_ARRAY));
  }

  @Data
  public static class Value {
    private final byte[] value;

    @EntityCreator
    public Value(final byte[] value) {
      this.value = value;
    }

    @EntityValue
    public byte[] getValue() {
      return value;
    }
  }

  @Test
  public void testValue() throws Exception {
    final StringEncoding<Value> e = encodingFor(Value.class);
    roundTrip(e, new Value(new byte[]{(byte) 0x42}));
  }

  @Data
  public static class DecodeNullListFrom {
    private final List<Optional<String>> field;
  }

  @Data
  public static class DecodeNullList {
    private final List<String> field;
  }

  /**
   * Immediate null entries in an array will be ignored, making them equivalent to absent values.
   */
  @Test
  public void testDecodeNullList() {
    final String reference =
        encodingFor(DecodeNullListFrom.class, DatabindOptions.OPTIONAL_EMPTY_AS_NULL).encode(
            new DecodeNullListFrom(
                ImmutableList.of(Optional.empty(), Optional.empty(), Optional.empty())));

    final StringEncoding<DecodeNullList> e = encodingFor(DecodeNullList.class);
    assertThat(e.decode(reference), is(new DecodeNullList(ImmutableList.of())));
  }

  @Data
  public static class EncodeNullList {
    private final List<Optional<String>> field;
  }

  @Test
  public void testEncodeNullList() {
    final int count = 3;

    final List<Optional<String>> result = new ArrayList<>();
    IntStream.range(0, count).forEach(i -> result.add(Optional.empty()));

    final StringEncoding<EncodeNullList> e = encodingFor(EncodeNullList.class);

    final String s = e.encode(new EncodeNullList(result));
    final EncodeNullList r = e.decode(s);

    assertThat(r, is(new EncodeNullList(ImmutableList.of())));
  }

  @Test
  public void testEncodeNullMap() {
    final int count = 3;

    final HashMap<String, Optional<String>> result = new HashMap<>();
    IntStream.range(0, count).forEach(i -> result.put(Integer.toString(i), Optional.empty()));

    final StringEncoding<Map<String, Optional<String>>> e = encodingFor(OPTIONAL_STRING_MAP);

    final String s = e.encode(result);
    final Map<String, Optional<String>> r = e.decode(s);

    assertThat(r, is(ImmutableMap.of()));
  }

  @Data
  public static class DecodeOptionalNullList {
    private final List<Optional<String>> field;
  }

  /**
   * An array forAnnotation null's will be decoded to empties if possible.
   */
  @Test
  public void testDecodeOptionalNullList() {
    final int count = 3;

    final StringEncoding<DecodeOptionalNullList> e =
        encodingFor(DecodeOptionalNullList.class, DatabindOptions.OPTIONAL_EMPTY_AS_NULL);

    final List<Optional<String>> result = new ArrayList<>();
    IntStream.range(0, count).forEach(i -> result.add(Optional.empty()));

    final DecodeOptionalNullList from = new DecodeOptionalNullList(result);

    final String encoded = e.encode(from);
    assertThat(e.decode(encoded), is(new DecodeOptionalNullList(result)));
  }

  @EntitySubTypes(
      {@EntitySubTypes.Type(AbstractType.A.class), @EntitySubTypes.Type(AbstractType.B.class)})
  interface AbstractType {
    @Data
    @EntityTypeName("a")
    class A implements AbstractType {
      private final String field;
    }

    @Data
    @EntityTypeName("empty")
    class B implements AbstractType {
    }
  }

  @Test
  public void testAbstractType() {
    StringEncoding<AbstractType> e = encodingFor(AbstractType.class);
    roundTrip(e, new AbstractType.A("a"));
    roundTrip(e, new AbstractType.B());
  }

  protected <E> void roundTrip(final StringEncoding<E> encoding, final E instance) {
    final String encoded = encoding.encode(instance);
    assertEquals(instance, encoding.decode(encoded));
  }

  @Data
  public static class Entry {
    private final double value;
  }
}
